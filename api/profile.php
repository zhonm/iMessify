<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, OPTIONS, POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Database connection parameters
$host = "localhost";
$db_name = "ztxitfbr_imessify";
$db_username = "ztxitfbr_imessify";
$db_password = "PuQWkCnHvRqfeM3Wcw58";

try {
    $conn = new PDO("mysql:host=$host;dbname=$db_name", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Handle POST request to update profile image
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $userId = $_POST['user_id'] ?? null;
        if (!$userId) {
            throw new Exception('User ID is required');
        }

        if (!isset($_FILES['image'])) {
            throw new Exception('No image file uploaded');
        }

        $file = $_FILES['image'];
        $fileName = uniqid() . '_' . basename($file['name']);
        $uploadDir = 'uploads/profile/';
        $uploadPath = $uploadDir . $fileName;

        if (!is_dir($uploadDir)) {
            mkdir($uploadDir, 0777, true);
        }

        if (move_uploaded_file($file['tmp_name'], $uploadPath)) {
            $imageUrl = 'https://yourdomain.com/api/' . $uploadPath;
            
            $stmt = $conn->prepare("UPDATE users SET profile_image = :image_url WHERE id = :user_id");
            $stmt->bindParam(":image_url", $imageUrl);
            $stmt->bindParam(":user_id", $userId);
            
            if ($stmt->execute()) {
                echo json_encode([
                    'success' => true,
                    'message' => 'Profile image updated successfully',
                    'imageUrl' => $imageUrl
                ]);
            } else {
                throw new Exception('Failed to update database');
            }
        } else {
            throw new Exception('Failed to upload image');
        }
    } 
    // Handle GET request to retrieve user profile
    else if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        if (!isset($_GET['user_id'])) {
            echo json_encode(["success" => false, "message" => "Missing user ID parameter"]);
            exit;
        }

        $userId = $conn->real_escape_string($_GET['user_id']);

        // Get user profile data
        $sql = "SELECT id, username, email, created_at, profile_image FROM users WHERE id = '$userId'";
        $result = $conn->query($sql);

        if ($result && $result->num_rows > 0) {
            $row = $result->fetch_assoc();
            
            $profile = [
                "id" => (int)$row['id'],
                "username" => $row['username'],
                "email" => $row['email'],
                "profile_image" => $row['profile_image'],
                "created_at" => $row['created_at']
            ];
            
            echo json_encode(["success" => true, "profile" => $profile]);
        } else {
            echo json_encode(["success" => false, "message" => "User not found"]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Invalid request method"]);
    }

} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage()
    ]);
}

$conn = null;
?>
