<?php

class FileHandler{
	
	private $con;
	
	public function __construct(){
		
		require_once dirname(__FILE__) .'/DbConnect.php';
		$db = new DbConnect();
		$this->con = $db->connect();
		
	}
	
	public function createUser($name,$email,$pass,$phone,$file, $extension){
		
		if(!$this->isEmailExist($email)){
			$filename = round(microtime(true) * 1000) . '.' . $extension;
			$filedest = dirname(__FILE__) . UPLOAD_PATH . $filename;
			move_uploaded_file($file, $filedest);
			
			$hash_password = password_hash($pass, PASSWORD_DEFAULT);
 
	
			$stmt = $this->con->prepare("INSERT INTO users (name,email,pass,phone,image) VALUES (?, ?, ?, ?, ?)");
			$stmt->bind_param("sssss", $name, $email, $hash_password, $phone,$filename);
			if ($stmt->execute()){
				
				return USER_CREATED;
			}
			else{
                return USER_FAILURE;
            }
		}
		return USER_EXISTS;
		
	}
	
	public function userLogin($email,$password){
		if($this->isEmailExist($email)){
            $hash_password = $this->getUserPasswordByEmail($email);
            if(password_verify($password,$hash_password)){
                return USER_AUTHENTICATED;
            }else{
                return USER_PASSWORD_DO_NOT_MATCH;
            }
        }else{
            return USER_NOT_FOUND;
        }
	}
	
	public function updateProfile($email,$name,$phone,$id){
		$stmt =  $this->con->prepare("UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?");
        $stmt->bind_param("sssi",$name,$email,$phone,$id);
        if($stmt->execute())
            return true;
        else
            return false;
	}
	
	public function updatePassword($currentpassword,$newpassword,$email){

        $hash_password = $this->getUserPasswordByEmail($email);

        if(password_verify($currentpassword,$hash_password)){
            
            $hash_password = password_hash($newpassword,PASSWORD_DEFAULT);
            $stmt = $this->con->prepare("UPDATE users SET pass = ? WHERE email = ?");
            $stmt->bind_param("ss",$hash_password,$email);
        
            if($stmt->execute())
                return PASSWORD_CHANGED;
            return PASSWORD_NOT_CHANGED;
        }else{
            return PASSWORD_DO_NOT_MATCH;
        }

    }
	
	public function deleteAccount($id){
		

        $stmt = $this->con->prepare("DELETE FROM users WHERE id = ? ");
        $stmt->bind_param("i",$id);

        if($stmt->execute())
            return true;
        else
            return false;

    }
	
	private function getUserPasswordByEmail($email){
		

       $stmt = $this->con->prepare("SELECT pass FROM users WHERE email = ?");
        $stmt->bind_param("s",$email);
        $stmt->execute();
        $stmt->bind_result($password);
        $stmt->fetch();
        return $password;
    }
	
	public function getUserProfileByEmail($email){
		
		$stmt = $this->con->prepare("SELECT id,name,email,phone,image FROM users WHERE email = ?");
		$stmt->bind_param("s",$email);
		$stmt->execute();
        $stmt->bind_result($id,$name,$email,$phone,$image);
		$stmt->fetch();
	
		$user = array();
        $user['id'] = $id;
		$user['name'] = $name;
        $user['email'] = $email;
        $user['phone'] = $phone;
		$user['image'] = $image;

        return $user;
		
	}
	
	private function isEmailExist($email){

        $stmt = $this->con->prepare("SELECT id FROM users WHERE email = ?");
        $stmt->bind_param("s",$email);
        $stmt->execute();
        $stmt->store_result();
        return $stmt->num_rows > 0;
    }
}
?>