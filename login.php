<?php
// Database connection parameters
$host = "localhost";
$db_name = "ztxitfbr_imessify";
$db_username = "ztxitfbr_imessify";  // Renamed to avoid conflict
$db_password = "PuQWkCnHvRqfeM3Wcw58";

// Set headers for JSON response
header('Content-Type: application/json');

// Initialize response array
$response = array(
    "success" => false,
    "message" => "",
    "username" => null
);

try {
    // Check if request is POST
    if ($_SERVER["REQUEST_METHOD"] !== "POST") {
        throw new Exception("Invalid request method");
    }

    // Get POST data (JSON from Android app)
    $json_data = file_get_contents('php://input');
    $data = json_decode($json_data, true);

    // Validate input
    if (!isset($data['username']) || !isset($data['password'])) {
        throw new Exception("Missing required fields");
    }

    $user_email = $data['username'];  // This is actually the email from app
    $user_password = $data['password'];

    // Connect to database using DB credentials, not user input
    $conn = new PDO("mysql:host=$host;dbname=$db_name", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Prepare query to check user credentials
    $stmt = $conn->prepare("SELECT * FROM users WHERE email = :email LIMIT 1");
    $stmt->bindParam(":email", $user_email);  // Use the user's email here
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Verify password (assuming password is hashed in database)
        if (password_verify($user_password, $user['password'])) {
            $response["success"] = true;
            $response["message"] = "Login successful";
            $response["username"] = $user['username'];
        } else {
            $response["message"] = "Invalid password";
        }
    } else {
        $response["message"] = "User not found";
    }

} catch (Exception $e) {
    $response["message"] = "Error: " . $e->getMessage();
}

// Return JSON response
echo json_encode($response);
?>