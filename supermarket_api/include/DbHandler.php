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

           
            // insert query
            $stmt = $this->conn->prepare("INSERT INTO user(name, email, password_hash, supermarket, status) values(?, ?, ?, ?, 1)");
            $stmt->bind_param("ssss", $name, $email, $password_hash, $supermarket);

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
        $stmt = $this->conn->prepare("SELECT name, email, status, created_at, supermarket FROM user WHERE email = ?");
        $stmt->bind_param("s", $email);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($name, $email, $status, $created_at, $supermarket);
            $stmt->fetch();
            $user = array();
            $user["name"] = $name;
            $user["email"] = $email;
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
     
 
          $sql = "SELECT * from products order by DateAdded DESC LIMIT 7";
                 //Getting result 
          $result = mysqli_query($this->conn,$sql); 
 
               //Adding results to an array 
            $res = array(); 
            
            while($row = mysqli_fetch_array($result)){
            array_push($res, array(
            "name"=>$row['ProductName'],
             "description"=>$row['Description'],

            "image"=>$row['image'],
            "date"=>$row['DateAdded']
            )
            );
            }
           
            //Displaying the array in json format 
           return  json_encode($res);
            }
public function getUsers() {
     
 
          $sql = "SELECT * from user";
                 //Getting result 
          $result = mysqli_query($this->conn,$sql); 
 
               //Adding results to an array 
            $res = array(); 
            
            while($row = mysqli_fetch_array($result)){
            array_push($res, array(
                            "id"=>$row['id'],

            "name"=>$row['name'],
             "supermarket"=>$row['supermarket'],

            "email"=>$row['email']
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
     
 
          $sql = "SELECT * from prices where ProductName='$product_name' ";
                 //Getting result 
          $result = mysqli_query($this->conn,$sql); 
 
               //Adding results to an array 
            $res = array(); 
            
            while($row = mysqli_fetch_array($result)){
            array_push($res, array(
            "supermarket"=>$row['Supermarket'],
            "price"=>$row['Price']
            )
            );
            }
           
            //Displaying the array in json format 
           return  json_encode($res);
            }
        
    
 /**
     * Deleting a task
     * @param String $task_id id of the task to delete
     */
    public function deleteUser($id) {
        $stmt = $this->conn->prepare("DELETE FROM user WHERE id = ?");

        $stmt->bind_param("i", $id);
        $stmt->execute();
        $num_affected_rows = $stmt->affected_rows;
        $stmt->close();
        return $num_affected_rows > 0;
    }
    public function deleteproduct($id) {
        $stmt = $this->conn->prepare("DELETE FROM products WHERE id = ?");

        $stmt->bind_param("i", $id);
        $stmt->execute();
        $num_affected_rows = $stmt->affected_rows;
        $stmt->close();
        return $num_affected_rows > 0;
    }
   
}

?>
