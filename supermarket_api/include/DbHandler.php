<?php

/**
 * Class to handle all db operations
 * This class will have CRUD methods for database tables
 *
 * @author Benson
 * @link URL Tutorial link
 */
class DbHandler {

    private $conn;

    function __construct() {
        require_once dirname(__FILE__) . '/DbConnect.php';
        // opening db connection
        $db = new DbConnect();
        $this->conn = $db->connect();
    }

    /* ------------- `users` table method ------------------ */

    /**
     * Creating new user
     * @param String $name User full name
     * @param String $email User login email id
     * @param String $password User login password
     */
    public function createUser($name, $email, $password, $supermarket) {
        require_once 'PassHash.php';
        $response = array();

        // First check if user already existed in db
        if (!$this->isUserExists($email)) {
            // Generating password hash
            $password_hash = PassHash::hash($password);

            // Generating API key
            $api_key = $this->generateApiKey();

            // insert query
            $stmt = $this->conn->prepare("INSERT INTO user(name, email, password_hash, api_key, supermarket, status) values(?, ?, ?, ?, ?, 1)");
            $stmt->bind_param("sssss", $name, $email, $password_hash, $api_key, $supermarket);

            $result = $stmt->execute();

            $stmt->close();

            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                return USER_CREATED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return USER_CREATE_FAILED;
            }
        } else {
            // User with same email already existed in the db
            return USER_ALREADY_EXISTED;
        }

        return $response;
    }
/**
     * Creating new user
     * @param String $name User full name
     * @param String $email User login email id
     * @param String $password User login password
     */
    public function createProduct($name, $description, $price, $supermarket, $image) {
        $response = array();

        // First check if product already existed in db
        if (!$this->isProductExists($name)) {
           
           $sql ="SELECT ProductId FROM products ORDER BY ProductId ASC";

		    $res = mysqli_query($this->conn,$sql);
		
		    $id = 0;
		
		    while($row = mysqli_fetch_array($res)){
				$id = $row['ProductId'];
		    } 
		    $path = "uploads/$id.png";
		    $path2 = "../uploads/$id.png";

		    $actualpath = "http://192.168.137.1/supermarket_api/$path";
		
		
            // insert query
            $stmt = $this->conn->prepare("INSERT INTO products(ProductName, Description, ProductPrice, supermarket, image) values(?, ?, ?, ?, ?)");
            $stmt->bind_param("sssss", $name, $description, $price, $supermarket, $actualpath);

            $result = $stmt->execute();


            $stmt->close();

       
            // Check for successful insertion
            if ($result) {
                // Product successfully inserted
                 // insert query supermarket prices
            $stmt2 = $this->conn->prepare("INSERT INTO prices(ProductName,  Price, Supermarket) values(?, ?, ?)");
            $stmt2->bind_param("sss", $name, $price, $supermarket);

            $result2 = $stmt2->execute();
            
            $stmt2->close();
              file_put_contents($path2,base64_decode($image));
            if (!$result2){
                return PRODUCT_CREATE_FAILED;
            }
                return PRODUCT_CREATED_SUCCESSFULLY;
            } else {
                // Failed to create product
                return PRODUCT_CREATE_FAILED;
            }
        } else {
            // product with same name already existed in the db
            return PRODUCT_ALREADY_EXISTED;
        }

        return $response;
    }
public function createProductPrice($name, $price, $supermarket) {
        $response = array();

              // insert query supermarket prices
            $stmt = $this->conn->prepare("INSERT INTO prices(ProductName,  Price, Supermarket) values(?, ?, ?)");
            $stmt->bind_param("sss", $name, $price, $supermarket);

            $result = $stmt->execute();
            
            $stmt->close();
            
            // Check for successful insertion
            if ($result) {
              
                return PRODUCT_CREATED_SUCCESSFULLY;
            } else {
                // Failed to create product
                return PRODUCT_CREATE_FAILED;
            }

        return $response;
    }
    /**
     * Checking user login
     * @param String $email User login email id
     * @param String $password User login password
     * @return boolean User login status success/fail
     */
    public function checkLogin($email, $password) {
        // fetching user by email
        $stmt = $this->conn->prepare("SELECT password_hash FROM user WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->bind_result($password_hash);

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // Found user with the email
            // Now verify the password

            $stmt->fetch();

            $stmt->close();

            if (PassHash::check_password($password_hash, $password)) {
                // User password is correct
                return TRUE;
            } else {
                // user password is incorrect
                return FALSE;
            }
        } else {
            $stmt->close();

            // user not existed with the email
            return FALSE;
        }
    }

    /**
     * Checking for duplicate user by email address
     * @param String $email email to check in db
     * @return boolean
     */
    private function isUserExists($email) {
        $stmt = $this->conn->prepare("SELECT id from user WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }
 /**
     * Checking for duplicate user by email address
     * @param String $email email to check in db
     * @return boolean
     */
    private function isProductExists($name) {
        $stmt = $this->conn->prepare("SELECT ProductId from products WHERE ProductName = ?");
        $stmt->bind_param("s", $name);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }
    /**
     * Fetching user by email
     * @param String $email User email id
     */
    public function getUserByEmail($email) {
        $stmt = $this->conn->prepare("SELECT name, email, api_key, status, created_at, supermarket FROM user WHERE email = ?");
        $stmt->bind_param("s", $email);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($name, $email, $api_key, $status, $created_at, $supermarket);
            $stmt->fetch();
            $user = array();
            $user["name"] = $name;
            $user["email"] = $email;
            $user["api_key"] = $api_key;
            $user["status"] = $status;
            $user["supermarket"] = $supermarket;
            $user["created_at"] = $created_at;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }

    /**
     * Fetching user api key
     * @param String $user_id user id primary key in user table
     */
    public function getApiKeyById($user_id) {
        $stmt = $this->conn->prepare("SELECT api_key FROM users WHERE id = ?");
        $stmt->bind_param("i", $user_id);
        if ($stmt->execute()) {
            // $api_key = $stmt->get_result()->fetch_assoc();
            // TODO
            $stmt->bind_result($api_key);
            $stmt->close();
            return $api_key;
        } else {
            return NULL;
        }
    }

    /**
     * Fetching user id by api key
     * @param String $api_key user api key
     */
    public function getUserId($api_key) {
        $stmt = $this->conn->prepare("SELECT id FROM users WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        if ($stmt->execute()) {
            $stmt->bind_result($user_id);
            $stmt->fetch();
            // TODO
            // $user_id = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $user_id;
        } else {
            return NULL;
        }
    }

    /**
     * Validating user api key
     * If the api key is there in db, it is a valid key
     * @param String $api_key user api key
     * @return boolean
     */
    public function isValidApiKey($api_key) {
        $stmt = $this->conn->prepare("SELECT id from users WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }

    /**
     * Generating random Unique MD5 String for user Api key
     */
    private function generateApiKey() {
        return md5(uniqid(rand(), true));
    }

    /* ------------- `tasks` table method ------------------ */

    /**
     * Creating new task
     * @param String $user_id user id to whom task belongs to
     * @param String $task task text
     */
    public function createTask($user_id, $task) {
        $stmt = $this->conn->prepare("INSERT INTO tasks(task) VALUES(?)");
        $stmt->bind_param("s", $task);
        $result = $stmt->execute();
        $stmt->close();

        if ($result) {
            // task row created
            // now assign the task to user
            $new_task_id = $this->conn->insert_id;
            $res = $this->createUserTask($user_id, $new_task_id);
            if ($res) {
                // task created successfully
                return $new_task_id;
            } else {
                // task failed to create
                return NULL;
            }
        } else {
            // task failed to create
            return NULL;
        }
    }

    /**
     * Fetching single task
     * @param String $task_id id of the task
     */
    public function getTask($task_id, $user_id) {
        $stmt = $this->conn->prepare("SELECT t.id, t.task, t.status, t.created_at from tasks t, user_tasks ut WHERE t.id = ? AND ut.task_id = t.id AND ut.user_id = ?");
        $stmt->bind_param("ii", $task_id, $user_id);
        if ($stmt->execute()) {
            $res = array();
            $stmt->bind_result($id, $task, $status, $created_at);
            // TODO
            // $task = $stmt->get_result()->fetch_assoc();
            $stmt->fetch();
            $res["id"] = $id;
            $res["task"] = $task;
            $res["status"] = $status;
            $res["created_at"] = $created_at;
            $stmt->close();
            return $res;
        } else {
            return NULL;
        }
    }
 /**
     * Fetching products
     * @param String $page_id id of the products list
     */
    public function getProduct($page_id) {
        //Initially we show the data from 1st row that means the 0th row 
        $start = 0; 
        //Limit is 1 that means we will show 1 items at once
        $limit = 2; 
        //Counting the total item available in the database 
          $total = mysqli_num_rows(mysqli_query($this->conn, "SELECT id from feed "));

           //We can go atmost to page number total/limit
            $page_limit = $total/$limit; 
            
            //If the page number is more than the limit we cannot show anything 
            if($page_id<=$page_limit){
            
            //Calculating start for every given page number
            $start = ($page_id - 1) * $limit; 
            
            //SQL query to fetch data of a range 
            $sql = "SELECT * from feed limit $start, $limit";
            
            //Getting result 
            $result = mysqli_query($this->conn,$sql); 
            
            //Adding results to an array 
            $res = array(); 
            
            while($row = mysqli_fetch_array($result)){
            array_push($res, array(
            "name"=>$row['name'],
            "publisher"=>$row['publisher'],
            "image"=>$row['image'])
            );
            }
            //Displaying the array in json format 
           return  json_encode($res);
            }else{
                      return NULL;
                }
           
      
    }
    /**
     * Fetching Products
     * @param String $page_id id of the Sellers list
     */
public function getProducts() {
     
 
          $sql = "SELECT * from products";
                 //Getting result 
          $result = mysqli_query($this->conn,$sql); 
 
               //Adding results to an array 
            $res = array(); 
            
            while($row = mysqli_fetch_array($result)){
            array_push($res, array(
            "name"=>$row['ProductName'],
             "description"=>$row['Description'],

            "image"=>$row['image']
            )
            );
            }
           
            //Displaying the array in json format 
           return  json_encode($res);
            }
/**
     * Fetching supermarket Products
     * @param String $page_id id of the Sellers list
     */
public function getSupermarketProducts() {
     
 
          $sql = "SELECT * from products";
                 //Getting result 
          $result = mysqli_query($this->conn,$sql); 
 
               //Adding results to an array 
            $res = array(); 
            
            while($row = mysqli_fetch_array($result)){
            array_push($res, array(
            "productId"=>$row['ProductId'],
            "name"=>$row['ProductName'],
             "description"=>$row['Description'],
            
             "image"=>$row['image']
            )
            );
            }
           
            //Displaying the array in json format 
           return  json_encode($res);
 }
    /**
     * Fetching Sellers
     * @param String $page_id id of the Sellers list
     */
public function getSellers($product_name) {
     
 
          $sql = "SELECT * from product where ProductName='$product_name' ";
                 //Getting result 
          $result = mysqli_query($this->conn,$sql); 
 
               //Adding results to an array 
            $res = array(); 
            
            while($row = mysqli_fetch_array($result)){
            array_push($res, array(
            "name"=>$row['ProductName'],
            "image"=>$row['image']
            )
            );
            }
           
            //Displaying the array in json format 
           return  json_encode($res);
            }
        
    /**
     * Fetching all user tasks
     * @param String $user_id id of the user
     */
    public function getAllUserTasks($user_id) {
        $stmt = $this->conn->prepare("SELECT t.* FROM tasks t, user_tasks ut WHERE t.id = ut.task_id AND ut.user_id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $tasks = $stmt->get_result();
        $stmt->close();
        return $tasks;
    }
 
	
    /**
     * Updating task
     * @param String $task_id id of the task
     * @param String $task task text
     * @param String $status task status
     */
    public function updateTask($user_id, $task_id, $task, $status) {
        $stmt = $this->conn->prepare("UPDATE tasks t, user_tasks ut set t.task = ?, t.status = ? WHERE t.id = ? AND t.id = ut.task_id AND ut.user_id = ?");
        $stmt->bind_param("siii", $task, $status, $task_id, $user_id);
        $stmt->execute();
        $num_affected_rows = $stmt->affected_rows;
        $stmt->close();
        return $num_affected_rows > 0;
    }

    /**
     * Deleting a task
     * @param String $task_id id of the task to delete
     */
    public function deleteTask($user_id, $task_id) {
        $stmt = $this->conn->prepare("DELETE t FROM tasks t, user_tasks ut WHERE t.id = ? AND ut.task_id = t.id AND ut.user_id = ?");
        $stmt->bind_param("ii", $task_id, $user_id);
        $stmt->execute();
        $num_affected_rows = $stmt->affected_rows;
        $stmt->close();
        return $num_affected_rows > 0;
    }

    /* ------------- `user_tasks` table method ------------------ */

    /**
     * Function to assign a task to user
     * @param String $user_id id of the user
     * @param String $task_id id of the task
     */
    public function createUserTask($user_id, $task_id) {
        $stmt = $this->conn->prepare("INSERT INTO user_tasks(user_id, task_id) values(?, ?)");
        $stmt->bind_param("ii", $user_id, $task_id);
        $result = $stmt->execute();

        if (false === $result) {
            die('execute() failed: ' . htmlspecialchars($stmt->error));
        }
        $stmt->close();
        return $result;
    }
    /* Save Users
    */
   public function saveUser($username, $email, $phone, $token) {
            $response = array();

             // insert query
            $stmt = $this->conn->prepare("INSERT INTO android_users(username, email, phone, token) values(?, ?, ?, ?)");
            $stmt->bind_param("ssss", $username, $email, $phone, $token);

            $result = $stmt->execute();

            $stmt->close();

            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                return USER_SAVED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return USER_SAVED_FAILED;
            }
        

        return $response;
        }
    public function saveAttachment($name, $image) {
        $stmt = $this->conn->prepare("SELECT id FROM payeform ORDER BY id ASC");
        $res = $stmt->execute();
        $id = 0;
		
        while($row = mysqli_fetch_array($res)){
				$id = $row['id'];
		} 

    	 $path = "attachments/$id.png";
         $actualpath = "http://localhost/iTaxReturnsApp/wp-content/uploads/$path";

		

        $stmt = $this->conn->prepare("INSERT INTO payeform(photo) values(?)");
        $stmt->bind_param("i", $actualpath);
        $result = $stmt->execute();

         if (true === $result) {
                file_put_contents("http://localhost/iTaxReturnsApp/wp-content/uploads/attachments/$id.png",base64_decode($image));
	        }

        if (false === $result) {
            die('execute() failed: ' . htmlspecialchars($stmt->error));
        }
        $stmt->close();
        return $result;
    }
     public function createRegForm($fname, $mname, $lname, $email, $phone, $mpesaref) {
        $response = array();

             // insert query
            $stmt = $this->conn->prepare("INSERT INTO regform(fname, mname, lname, email, phone, mpesaref) values(?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("ssssss", $fname, $mname, $lname, $email, $phone, $mpesaref);

            $result = $stmt->execute();

            $stmt->close();

            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                return FORM_SAVED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return FORM_SAVED_FAILED;
            }
        

        return $response;
    }
     public function createNilForm($fname,$mname,$lname,$krapin,$krapass, $email, $phone,$mpesaref) {
        $response = array();

             // insert query
            $stmt = $this->conn->prepare("INSERT INTO nilform(fname, mname, lname,krapin,krapass,email, phone, mpesaref) values(?, ?, ?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("ssssssss", $fname, $mname, $lname, $krapin, $krapass, $email, $phone, $mpesaref);

            $result = $stmt->execute();

            $stmt->close();

            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                return FORM_SAVED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return FORM_SAVED_FAILED;
            }
        

        return $response;
    }
     public function createPayeForm($fname,$mname,$lname,$krapin,$krapass, $email, $phone,$mpesaref, $image) {
        $response = array();
        
        //$stmt = $this->conn->prepare("SELECT id FROM payeform ORDER BY id ASC");
      //  $res = $stmt->execute();
        $sql ="SELECT id FROM payeform ORDER BY id ASC";
		
		$res = mysqli_query($this->conn,$sql);
        $id = 0;
		
      
        while($row = mysqli_fetch_array($res)){
				$id = $row['id'];
		} 

    	 $path = "../../iTaxReturnsApp/wp-content/2016/$id.png";
         $actualpath = "http://localhost/iTaxReturnsApp/wp-content/uploads/$path";

		     // insert query
            $stmt = $this->conn->prepare("INSERT INTO payeform(fname, mname, lname, krapin, krapass, email, phone, mpesaref, image) values(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("sssssssss", $fname, $mname, $lname, $krapin, $krapass, $email, $phone, $mpesaref, $actualpath);

            $result = $stmt->execute();

            $stmt->close();

            // Check for successful insertion
            if ($result) {
           file_put_contents($path,base64_decode($image));

                // User successfully inserted
                return FORM_SAVED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return FORM_SAVED_FAILED;
            }
        

        return $response;
    }
  public function checkMpesaRef($mpesaref) {
        $response = array();

        // First check if user already existed in db
        if ($this->isMpesaRefExists($mpesaref)) {
            return MPESAREF_ALREADY_EXISTED;

      } else {
            // User with same email already existed in the db
            return MPESAREF_NOT_EXISTED;
        }

        return $response;
    }
  private function isMpesaRefExists($mpesaref) {
        $stmt = $this->conn->prepare("SELECT id from payments WHERE mpesaref = ?");
        $stmt->bind_param("s", $mpesaref);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }
     public function testcount() {
        $total = mysqli_num_rows(mysqli_query($this->conn, "SELECT id from feed "));
 
       
        return $total;
    }
}

?>
