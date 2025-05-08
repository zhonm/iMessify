<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, OPTIONS");
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

// Handle GET request to retrieve conversations
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (!isset($_GET['user_id'])) {
        echo json_encode(["success" => false, "message" => "Missing user ID parameter"]);
        exit;
    }

    $userId = $conn->real_escape_string($_GET['user_id']);

    // Get all conversations where the user is either sender or receiver
    $sql = "SELECT 
                m.sender_id, 
                m.receiver_id,
                CASE 
                    WHEN m.sender_id = '$userId' THEN m.receiver_id
                    ELSE m.sender_id
                END AS other_user_id,
                u.username as otherUsername,
                u.profile_image,
                MAX(m.created_at) AS last_updated,
                (SELECT message_text FROM messages WHERE 
                    ((sender_id = m.sender_id AND receiver_id = m.receiver_id) OR 
                    (sender_id = m.receiver_id AND receiver_id = m.sender_id))
                    ORDER BY created_at DESC LIMIT 1) AS last_message,
                (SELECT COUNT(*) FROM messages WHERE 
                    receiver_id = '$userId' AND sender_id = other_user_id AND is_read = 0) AS unread_count
            FROM messages m
            JOIN users u ON (
                CASE 
                    WHEN m.sender_id = '$userId' THEN m.receiver_id
                    ELSE m.sender_id
                END = u.id
            )
            WHERE m.sender_id = '$userId' OR m.receiver_id = '$userId'
            GROUP BY other_user_id
            ORDER BY last_updated DESC";

    $result = $conn->query($sql);

    if ($result) {
        $conversations = [];
        
        while ($row = $result->fetch_assoc()) {
            $conversations[] = [
                "id" => count($conversations) + 1,  // Generate sequence ID
                "otherUserId" => (int)$row['other_user_id'],
                "otherUsername" => $row['otherUsername'],
                "lastMessage" => $row['last_message'],
                "unreadCount" => (int)$row['unread_count'],
                "lastUpdated" => $row['last_updated'],
                "profileImage" => $row['profile_image'],
            ];
        }
        
        echo json_encode(["success" => true, "conversations" => $conversations]);
    } else {
        echo json_encode(["success" => false, "message" => "Error fetching conversations: " . $conn->error]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
}

$conn->close();
?>