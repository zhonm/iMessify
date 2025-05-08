<?php
// contacts.php

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
    "message" => ""
);

try {
    // Connect to database
    $conn = new PDO("mysql:host=$host;dbname=$db_name", $db_username, $db_password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Handle different HTTP methods
    $method = $_SERVER['REQUEST_METHOD'];

    switch ($method) {
        case 'GET':
            // Get contacts for user
            if (!isset($_GET['user_id'])) {
                throw new Exception("User ID is required");
            }

            $user_id = $_GET['user_id'];

            $stmt = $conn->prepare("SELECT * FROM contacts WHERE user_id = :user_id ORDER BY name ASC");
            $stmt->bindParam(":user_id", $user_id);
            $stmt->execute();

            $contacts = $stmt->fetchAll(PDO::FETCH_ASSOC);

            $response["success"] = true;
            $response["message"] = "Contacts retrieved successfully";
            $response["contacts"] = $contacts;
            break;

        case 'POST':
            // Check if this is a multipart form request or JSON request
            $contentType = isset($_SERVER["CONTENT_TYPE"]) ? $_SERVER["CONTENT_TYPE"] : "";
            
            if (strpos($contentType, "multipart/form-data") !== false) {
                // Handle multipart form data with image upload
                if (!isset($_POST['user_id']) || !isset($_POST['name']) || !isset($_POST['username'])) {
                    throw new Exception("Missing required fields");
                }

                $user_id = $_POST['user_id'];
                $name = $_POST['name'];
                $username = $_POST['username'];
                $image_url = null;

                // Handle file upload
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
                            // Create full URL to the image
                            $image_url = 'https://imessify.x10.mx/' . $targetPath;
                        } else {
                            throw new Exception("Failed to upload image");
                        }
                    } else {
                        throw new Exception("Invalid file type. Only JPG, JPEG, PNG are allowed");
                    }
                }

                $stmt = $conn->prepare("INSERT INTO contacts (user_id, name, username, image_url) VALUES (:user_id, :name, :username, :image_url)");
                $stmt->bindParam(":user_id", $user_id);
                $stmt->bindParam(":name", $name);
                $stmt->bindParam(":username", $username);
                $stmt->bindParam(":image_url", $image_url);
                $stmt->execute();

                $contact_id = $conn->lastInsertId();

                $response["success"] = true;
                $response["message"] = "Contact added successfully";
                $response["contactId"] = $contact_id;
            } else {
                // Handle regular JSON request
                $json_data = file_get_contents('php://input');
                $data = json_decode($json_data, true);

                if (!isset($data['userId']) || !isset($data['name']) || !isset($data['username'])) {
                    throw new Exception("Missing required fields");
                }

                $user_id = $data['userId'];
                $name = $data['name'];
                $username = $data['username'];
                $image_url = isset($data['imageUrl']) ? $data['imageUrl'] : null;

                $stmt = $conn->prepare("INSERT INTO contacts (user_id, name, username, image_url) VALUES (:user_id, :name, :username, :image_url)");
                $stmt->bindParam(":user_id", $user_id);
                $stmt->bindParam(":name", $name);
                $stmt->bindParam(":username", $username);
                $stmt->bindParam(":image_url", $image_url);
                $stmt->execute();

                $contact_id = $conn->lastInsertId();

                $response["success"] = true;
                $response["message"] = "Contact added successfully";
                $response["contactId"] = $contact_id;
            }
            break;

        case 'PUT':
            // Update contact
            if (!isset($_GET['id'])) {
                throw new Exception("Contact ID is required");
            }

            $contact_id = $_GET['id'];

            $json_data = file_get_contents('php://input');
            $data = json_decode($json_data, true);

            if (!isset($data['name']) || !isset($data['username'])) {
                throw new Exception("Missing required fields");
            }

            $user_id = $data['userId'];
            $name = $data['name'];
            $username = $data['username'];
            $image_url = isset($data['imageUrl']) ? $data['imageUrl'] : null;

            $stmt = $conn->prepare("UPDATE contacts SET name = :name, username = :username, image_url = :image_url WHERE id = :id AND user_id = :user_id");
            $stmt->bindParam(":id", $contact_id);
            $stmt->bindParam(":user_id", $user_id);
            $stmt->bindParam(":name", $name);
            $stmt->bindParam(":username", $username);
            $stmt->bindParam(":image_url", $image_url);
            $stmt->execute();

            if ($stmt->rowCount() > 0) {
                $response["success"] = true;
                $response["message"] = "Contact updated successfully";
                $response["contactId"] = $contact_id;
            } else {
                $response["message"] = "No changes made or contact not found";
            }
            break;

        case 'DELETE':
            // Delete contact
            if (!isset($_GET['id'])) {
                throw new Exception("Contact ID is required");
            }

            $contact_id = $_GET['id'];

            $stmt = $conn->prepare("DELETE FROM contacts WHERE id = :id");
            $stmt->bindParam(":id", $contact_id);
            $stmt->execute();

            if ($stmt->rowCount() > 0) {
                $response["success"] = true;
                $response["message"] = "Contact deleted successfully";
            } else {
                $response["message"] = "Contact not found";
            }
            break;

        default:
            throw new Exception("Invalid request method");
    }
} catch (Exception $e) {
    $response["message"] = "Error: " . $e->getMessage();
}

// Return JSON response
echo json_encode($response);
?>