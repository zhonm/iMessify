<?php
// Database connection settings
$servername = "localhost"; // x10hosting uses localhost
$username = "ztxitfbr_imessify"; // your database username
$password = "PuQWkCnHvRqfeM3Wcw58"; // your database password
$dbname = "ztxitfbr_imessify"; // your database name

// Set header to return JSON
header('Content-Type: application/json');

// Handle only POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Only POST requests allowed']);
    exit;
}

// Get JSON data from the request
$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

// Validate required fields
if (!isset($data['email']) || !isset($data['password'])) {
    echo json_encode(['success' => false, 'message' => 'Missing required fields']);
    exit;
}

// Create database connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    echo json_encode(['success' => false, 'message' => 'Connection failed']);
    exit;
}

// Sanitize inputs
$email = $conn->real_escape_string(trim($data['email']));
$password = $data['password'];

// Find user by email
$sql = "SELECT * FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(['success' => false, 'message' => 'Email not found']);
    $stmt->close();
    $conn->close();
    exit;
}

// Get user data
$user = $result->fetch_assoc();

// Verify password
if (!password_verify($password, $user['password'])) {
    echo json_encode(['success' => false, 'message' => 'Incorrect password']);
    $stmt->close();
    $conn->close();
    exit;
}

// Login successful, return user data
echo json_encode([
    'success' => true,
    'message' => 'Login successful',
    'userId' => $user['id'],
    'username' => $user['username']
]);

$stmt->close();
$conn->close();
?>

