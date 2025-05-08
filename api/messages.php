<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Database connection
$host = "localhost";
$db_name = "ztxitfbr_imessify";
$db_username = "ztxitfbr_imessify";
$db_password = "PuQWkCnHvRqfeM3Wcw58";

// Create connection
$conn = new mysqli($host, $db_username, $db_password, $db_name);

// Check connection
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Connection failed: " . $conn->connect_error]));
}

// Get request method
$method = $_SERVER['REQUEST_METHOD'];

// Handle different request methods
switch ($method) {
    case 'GET':
        // Get messages between users
        getMessages($conn);
        break;
    case 'POST':
        // Send a new message
        sendMessage($conn);
        break;
    default:
        echo json_encode(["success" => false, "message" => "Invalid request method"]);
        break;
}

function getMessages($conn) {
    // Get JSON data from the request
    $data = json_decode(file_get_contents('php://input'), true);
    
    // If GET parameters are used instead of JSON body
    if (!$data) {
        $data = $_GET;
    }
    
    // Validate required parameters
    if (!isset($data['user_id']) || !isset($data['contact_id'])) {
        echo json_encode(["success" => false, "message" => "Missing required parameters"]);
        return;
    }
    
    $userId = $conn->real_escape_string($data['user_id']);
    $contactId = $conn->real_escape_string($data['contact_id']);
    
    // Get messages between the two users (in both directions)
    $sql = "SELECT m.*, u_sender.username AS sender_username, u_receiver.username AS receiver_username 
            FROM messages m
            JOIN users u_sender ON m.sender_id = u_sender.id
            JOIN users u_receiver ON m.receiver_id = u_receiver.id
            WHERE (m.sender_id = '$userId' AND m.receiver_id = '$contactId') 
               OR (m.sender_id = '$contactId' AND m.receiver_id = '$userId')
            ORDER BY m.created_at ASC";
    
    $result = $conn->query($sql);
    
    if ($result) {
        $messages = [];
        while ($row = $result->fetch_assoc()) {
            $messages[] = [
                "id" => $row['id'],
                "sender_id" => $row['sender_id'],
                "receiver_id" => $row['receiver_id'],
                "sender_username" => $row['sender_username'],
                "receiver_username" => $row['receiver_username'],
                "message" => $row['message_text'],
                "is_read" => (bool)$row['is_read'],
                "timestamp" => $row['created_at']
            ];
        }
        
        // Mark all received messages as read
        $updateSql = "UPDATE messages SET is_read = 1 
                     WHERE receiver_id = '$userId' AND sender_id = '$contactId' AND is_read = 0";
        $conn->query($updateSql);
        
        echo json_encode(["success" => true, "messages" => $messages]);
    } else {
        echo json_encode(["success" => false, "message" => "Error fetching messages: " . $conn->error]);
    }
}

function sendMessage($conn) {
    // Get JSON data from the request
    $data = json_decode(file_get_contents('php://input'), true);
    
    // If POST parameters are used instead of JSON body
    if (!$data || empty($data)) {
        $data = $_POST;
    }
    
    // If GET/URL parameters are used
    if (empty($data) && isset($_GET['sender_id'])) {
        $data = $_GET;
    }
    
    // Validate required parameters
    if (!isset($data['sender_id']) || !isset($data['receiver_name']) || !isset($data['message'])) {
        echo json_encode([
            "success" => false, 
            "message" => "Missing required parameters. Needed: sender_id, receiver_name, message",
            "received" => $data
        ]);
        return;
    }
    
    $senderId = $conn->real_escape_string($data['sender_id']);
    $receiverName = $conn->real_escape_string($data['receiver_name']);
    $messageText = $conn->real_escape_string($data['message']);
    
    // Find receiver ID by username
    $sql = "SELECT id FROM users WHERE username = '$receiverName'";
    $result = $conn->query($sql);
    
    if ($result && $result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $receiverId = $row['id'];
        
        // Insert the message
        $insertSql = "INSERT INTO messages (sender_id, receiver_id, message_text) 
                      VALUES ('$senderId', '$receiverId', '$messageText')";
        
        if ($conn->query($insertSql) === TRUE) {
            echo json_encode([
                "success" => true, 
                "message" => "Message sent successfully",
                "receiver_id" => $receiverId
            ]);
        } else {
            echo json_encode(["success" => false, "message" => "Error sending message: " . $conn->error]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Recipient not found"]);
    }
}

// Function to get unread message count
function getUnreadMessageCount($conn) {
    $data = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($data['user_id'])) {
        echo json_encode(["success" => false, "message" => "Missing user ID"]);
        return;
    }
    
    $userId = $conn->real_escape_string($data['user_id']);
    
    $sql = "SELECT COUNT(*) as count FROM messages WHERE receiver_id = '$userId' AND is_read = 0";
    $result = $conn->query($sql);
    
    if ($result) {
        $row = $result->fetch_assoc();
        echo json_encode(["success" => true, "unread_count" => $row['count']]);
    } else {
        echo json_encode(["success" => false, "message" => "Error fetching unread message count"]);
    }
}

// Close connection
$conn->close();
?>