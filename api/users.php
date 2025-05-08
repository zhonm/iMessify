<?php
// users.php - API endpoint for user search functionality

// Database connection parameters
$host = "localhost";
$db_name = "ztxitfbr_imessify";
$db_username = "ztxitfbr_imessify";
$db_password = "PuQWkCnHvRqfeM3Wcw58";

// Set headers for JSON response
header('Content-Type: application/json');

// Initialize response array
$response = array(
    "success" => false,
    "message" => "",
    "users" => array()
);

try {
    // Connect to database
    $conn = new PDO("mysql:host=$host;dbname=$db_name", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Handle GET request for searching users
    $method = $_SERVER['REQUEST_METHOD'];
    
    if ($method === 'GET') {
        // Check if we're looking for specific username or searching
        if (isset($_GET['username'])) {
            // Get user by exact username match for messaging
            $username = $_GET['username'];
            $stmt = $conn->prepare("SELECT id, username, email, profile_image FROM users WHERE username = :username LIMIT 1");
            $stmt->bindParam(":username", $username);
            $stmt->execute();
            
            $user = $stmt->fetch(PDO::FETCH_ASSOC);
            
            if ($user) {
                $response["success"] = true;
                $response["message"] = "User found";
                $response["user_id"] = $user['id'];
                $response["username"] = $user['username'];
                $response["email"] = $user['email'];
                $response["profile_image"] = $user['profile_image'];
            } else {
                $response["success"] = false;
                $response["message"] = "User not found: $username";
            }
        }
        // Original search functionality
        else if (isset($_GET['search'])) {
            $search_term = $_GET['search'];
            if (strlen($search_term) < 2) {
                throw new Exception("Search query must be at least 2 characters long");
            }

            // Prepare SQL query for user search
            $stmt = $conn->prepare("SELECT id, username, email, profile_image FROM users WHERE username LIKE :search LIMIT 20");
            $search_param = "%" . $search_term . "%";
            $stmt->bindParam(":search", $search_param);
            $stmt->execute();

            $users = $stmt->fetchAll(PDO::FETCH_ASSOC);

            $response["success"] = true;
            $response["message"] = "Search completed successfully";
            $response["users"] = $users;
        } else {
            throw new Exception("Either 'search' or 'username' parameter is required");
        }
    } else {
        throw new Exception("Invalid request method. Only GET is supported.");
    }
} catch (Exception $e) {
    $response["message"] = "Error: " . $e->getMessage();
}

// Return JSON response
echo json_encode($response);
?>