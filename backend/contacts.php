<?php
header('Content-Type: application/json');
require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $response = array();
    
    // Get POST data
    $userId = $_POST['user_id'];
    $name = $_POST['name'];
    $phone = $_POST['phone'];
    
    // Handle file upload
    $imageUrl = null;
    if (isset($_FILES['profile_picture']) && $_FILES['profile_picture']['error'] === 0) {
        $uploadDir = 'uploads/';
        if (!file_exists($uploadDir)) {
            mkdir($uploadDir, 0777, true);
        }
        
        // Generate unique filename
        $filename = time() . '_' . basename($_FILES['profile_picture']['name']);
        $targetPath = $uploadDir . $filename;
        
        // Check if image file is valid
        $validTypes = array('jpg', 'jpeg', 'png');
        $fileType = strtolower(pathinfo($targetPath, PATHINFO_EXTENSION));
        
        if (in_array($fileType, $validTypes)) {
            if (move_uploaded_file($_FILES['profile_picture']['tmp_name'], $targetPath)) {
                $imageUrl = 'https://imessify.x10.mx/' . $targetPath;
            }
        }
    }
    
    // Insert into database
    $stmt = $conn->prepare("INSERT INTO contacts (user_id, name, phone, image_url) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("isss", $userId, $name, $phone, $imageUrl);
    
    if ($stmt->execute()) {
        $response = array(
            'success' => true,
            'message' => 'Contact added successfully',
            'contactId' => $stmt->insert_id
        );
    } else {
        $response = array(
            'success' => false,
            'message' => 'Error adding contact: ' . $stmt->error
        );
    }
    
    $stmt->close();
    $conn->close();
    
    echo json_encode($response);
}
?>
