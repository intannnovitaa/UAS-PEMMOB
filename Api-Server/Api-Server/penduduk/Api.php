<?php

require_once dirname(__FILE__) . '/FileHandler.php';

$response = array();
if (isset($_GET['apicall'])) {
	switch ($_GET['apicall']) {
		
		case 'register':

            if (isset($_POST['name']) && strlen($_POST['name']) > 0 &&
				isset($_POST['email']) && strlen($_POST['email']) > 0 &&
				isset($_POST['pass']) && strlen($_POST['pass']) > 0 &&
				isset($_POST['phone']) && strlen($_POST['phone']) > 0 && 
				$_FILES['image']['error'] === UPLOAD_ERR_OK) {
					
                $upload = new FileHandler();

                $file = $_FILES['image']['tmp_name'];

              
				$name = $_POST['name'];
				$email = $_POST['email'];
				$pass = $_POST['pass'];
				$phone = $_POST['phone'];
				
				$result = $upload->createUser($name,$email,$pass,$phone,$file, getFileExtension($_FILES['image']['name']), $name);
				
				if($result == USER_CREATED){
					$response['error'] = false;
                    $response['message'] = 'Registation Successfullly';
					$user = $upload->getUserProfileByEmail($email);
					$response['user'] = $user;
				}else if($result == USER_FAILURE){
					$response['error'] = true; 
					$response['message'] = 'Some error occurred';
				}else if($result == USER_EXISTS){
					$response['error'] = true; 
					$response['message'] = 'User Already Exists';
				}

            } else {
                $response['error'] = true;
                $response['message'] = 'Required parameters are not available';
            }
            break;
			
		case 'userLogin':
				if (isset($_POST['email']) && strlen($_POST['email']) > 0 &&
					isset($_POST['pass']) && strlen($_POST['pass']) > 0){
						
						
						
					$upload = new FileHandler();
					$email = $_POST['email'];
					$pass = $_POST['pass'];
					
					$result = $upload->userLogin($email,$pass);
					
					if($result == USER_AUTHENTICATED){
						$response['error'] = false;
						$response['message'] = 'User LoginSuccessFull.';
						$user = $upload->getUserProfileByEmail($email);
						$response['user'] = $user;
					}else if($result == USER_NOT_FOUND){
						$response['error'] = true;
						$response['message'] = 'User Not Exists.';
					}else if($result == USER_PASSWORD_DO_NOT_MATCH){
						$response['error'] = true;
						$response['message'] = 'Invalid User Credential.';
					}
				}
				else {
					$response['error'] = true;
					$response['message'] = 'Required parameters are not available';
				}
				break;
				
		case 'updateProfile':
	
			if (isset($_POST['id']) && strlen($_POST['id']) > 0 &&
				isset($_POST['name']) && strlen($_POST['name']) > 0 &&
				isset($_POST['email']) && strlen($_POST['email']) > 0 &&
				isset($_POST['phone']) && strlen($_POST['phone']) > 0)
				{
				
					$upload = new FileHandler();
					
					$id = $_POST['id'];
					$name = $_POST['name'];
					$email = $_POST['email'];
					$phone = $_POST['phone'];

					if($upload->updateProfile($email,$name,$phone,$id)){
						$response['error'] = false;
						$response['message'] = 'Profile Updated Successfullly';
						$user = $upload->getUserProfileByEmail($email);
						$response['user'] = $user;
					}else{
						$response['error'] = true;
						$response['message'] = 'Please try again later.';
					}
					
				
				}else{
					$response['error'] = true;
					$response['message'] = 'Required parameters are not available';
				}
	
			
			break;
			
		case 'updatePassword':
		
			if (isset($_POST['currentpass']) && strlen('currentpass') > 0 &&
				isset($_POST['newpass']) && strlen($_POST['newpass']) > 0 &&
				isset($_POST['email']) && strlen($_POST['email']) > 0 ){
					
					
					
					
				$upload = new FileHandler();
					
				$current_pass = $_POST['currentpass'];
				$newpass = $_POST['newpass'];
				$email = $_POST['email'];
				
				$result = $upload->updatePassword($current_pass,$newpass,$email);
				
				if($result == PASSWORD_CHANGED){
					$response['error'] = false;
					$response['message'] = 'Password Changed.';
				}else if($result == PASSWORD_DO_NOT_MATCH){
					$response['error'] = true;
					$response['message'] = 'You have given wrong password.';
				}else if($result == PASSWORD_NOT_CHNGED){
					$response['error'] = true;
					$response['message'] = 'Some error occurred.';
				}
					
			}else{
					$response['error'] = true;
					$response['message'] = 'Required parameters are not available';
			}
			break;
			
		case 'deleteAccount':
		if (isset($_POST['id']) && strlen('id') > 0){
			$upload = new FileHandler();
			$id = $_POST['id'];
			
			if($upload->deleteAccount($id)){
				$response['error'] = false;
				$response['message'] = 'User Has Been Deleted.';
			}else{
				$response['error'] = true;
				$response['message'] = 'Please try again later.';
			}
			
		}else{
			
		}
			break;
		case 'getuserprofile':

				$upload = new FileHandler();
				$response['error'] = false;
				$response['image'] = $upload->getAllFiles();
				
				

				break;
	}
}
echo json_encode($response);

function getFileExtension($file)
{
    $path_parts = pathinfo($file);
    return $path_parts['extension'];
}
?>