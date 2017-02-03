<?php

require_once '../include/DbHandler.php';
require_once '../include/PassHash.php';
require '.././libs/Slim/Slim.php';

\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();

// User id from db - Global Variable
$user_id = NULL;


/**
 * ----------- URLS ---------------------------------
 */
/**
 * User Registration
 * url - /register
 * method - POST
 * params - name, email, password
 */
$app->post('/register', function() use ($app) {
            // check for required params
            verifyRequiredParams(array('name', 'email', 'password', 'supermarket'));

            $response = array();

            // reading post params
            $name = $app->request->post('name');
            $email = $app->request->post('email');
            $password = $app->request->post('password');
            $supermarket = $app->request->post('supermarket');

            // validating email address
            validateEmail($email);

            $db = new DbHandler();
            $res = $db->createUser($name, $email, $password, $supermarket);

            if ($res == USER_CREATED_SUCCESSFULLY) {
                $response["error"] = false;
                $response["message"] = "You are successfully registered";
            } else if ($res == USER_CREATE_FAILED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while registering";
            } else if ($res == USER_ALREADY_EXISTED) {
                $response["error"] = true;
                $response["message"] = "Sorry, this email already existed";
            }
            // echo json response
            echoRespnse(201, $response);
        });


/**
 * User Login
 * url - /login
 * method - POST
 * params - email, password
 */
$app->post('/login', function() use ($app) {
            // check for required params
            verifyRequiredParams(array('email', 'password'));

            // reading post params
            $email = $app->request()->post('email');
            $password = $app->request()->post('password');
            $response = array();

            $db = new DbHandler();
            // check for correct email and password
            if ($db->checkLogin($email, $password)) {
                // get the user by email
                $user = $db->getUserByEmail($email);

                if ($user != NULL) {
                    $response["error"] = false;
                    $response['name'] = $user['name'];
                    $response['email'] = $user['email'];
                    $response['supermarket'] = $user['supermarket'];

                    $response['createdAt'] = $user['created_at'];
                } else {
                    // unknown error occurred
                    $response['error'] = true;
                    $response['message'] = "An error occurred. Please try again";
                }
            } else {
                // user credentials are wrong
                $response['error'] = true;
                $response['message'] = 'Login failed. Incorrect credentials';
            }

            echoRespnse(200, $response);
        });

 /**
 * Listing single product of particual user
 * method GET
 * url /products/:id
 */
$app->get('/get-products/:id', function($page_id) {
            $response = array();
            $db = new DbHandler();

            // fetch product
            $result = $db->getProduct($page_id);

            if ($result != NULL) {
               // $response["error"] = false;
                $response = $result;
                echo $response;
            } else {
               // $response["error"] = true;
                $response = "over";
                echo $response;
            }
        });
$app->get('/get-all-products', function() use ($app) {
            $response = array();

            $db = new DbHandler();

            // fetch sellers
            $result = $db->getProducts();

            if ($result != NULL) {
               // $response["error"] = false;
               $response = $result;
                echo '{"products":' . $response ."}";
            } else {
               // $response["error"] = true;
                $response = "not found";
                echoRespnse(200, $response);
            }
        });
$app->get('/get-all-supermarket-products', function() use ($app) {
            $response = array();

            $db = new DbHandler();

            // fetch sellers
            $result = $db->getSupermarketProducts();

            if ($result != NULL) {
               // $response["error"] = false;
               $response = $result;
                echo '{"supermarketProducts":' . $response ."}";
            } else {
               // $response["error"] = true;
                $response = "not found";
                echoRespnse(200, $response);
            }
        });
    
$app->post('/get-sellers', function() use ($app) {
            $response = array();
            $product_name = $app->request->post('product_name');

            $db = new DbHandler();

            // fetch sellers
            $result = $db->getSellers($product_name);

            if ($result != NULL) {
               // $response["error"] = false;
               $response = $result;
                echo '{"result":' . $response ."}";
            } else {
               // $response["error"] = true;
                $response = "not found";
                echoRespnse(200, $response);
            }
        });

$app->post('/add-product', function() use ($app) {
            // check for required params
            verifyRequiredParams(array('product_name', 'description', 'price', 'supermarket'));

            $response = array();

            // reading post params
            $name = $app->request->post('product_name');
            $description = $app->request->post('description');
            $price = $app->request->post('price');
            $supermarket = $app->request->post('supermarket');
            $image = $app->request->post('image');

           

            $db = new DbHandler();
            $res = $db->createProduct($name, $description, $price, $supermarket, $image);

            if ($res == PRODUCT_CREATED_SUCCESSFULLY) {
                $response["error"] = false;
                $response["message"] = "Product added successfully";
            } else if ($res == PRODUCT_CREATE_FAILED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while adding product detail(s)";
            } else if ($res == PRODUCT_ALREADY_EXISTED) {
                $response["error"] = true;
                $response["message"] = "Sorry, this product already existed";
            }
            // echo json response
            echoRespnse(201, $response);
        });
$app->post('/add-product-price', function() use ($app) {
            // check for required params
            verifyRequiredParams(array('product_name', 'price', 'supermarket'));

            $response = array();

            // reading post params
            $name = $app->request->post('product_name');
            $price = $app->request->post('price');
            $supermarket = $app->request->post('supermarket');

           

            $db = new DbHandler();
            $res = $db->createProductPrice($name,  $price, $supermarket);

            if ($res == PRODUCT_CREATED_SUCCESSFULLY) {
                $response["error"] = false;
                $response["message"] = "Price added successfully";
            } else if ($res == PRODUCT_CREATE_FAILED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while adding product detail(s)";
            }
            // echo json response
            echoRespnse(201, $response);
        });
$app->get('/get-all-users', function() use ($app) {
            $response = array();

            $db = new DbHandler();

            // fetch sellers
            $result = $db->getUsers();

            if ($result != NULL) {
               // $response["error"] = false;
               $response = $result;
                echo '{"user":' . $response ."}";
            } else {
               // $response["error"] = true;
                $response = "not found";
                echoRespnse(200, $response);
            }
        });
$app->post('/delete-user', function() use($app) {

        verifyRequiredParams(array('id'));


            // reading post params
             $id = $app->request->post('id');

         
            $db = new DbHandler();
            $response = array();
            $result = $db->deleteUser($id);
            if ($result) {
                // User deleted successfully
                $response["error"] = false;
                $response["message"] = "User deleted succesfully";
            } else {
                // User failed to delete
                $response["error"] = true;
                $response["message"] = "User failed to delete. Please try again!";
            }
            echoRespnse(200, $response);
        });
$app->post('/delete-product', function() use($app) {

        verifyRequiredParams(array('id'));


            // reading post params
             $id = $app->request->post('id');

         
            $db = new DbHandler();
            $response = array();
            $result = $db->deleteProduct($id);
            if ($result) {
                // User deleted successfully
                $response["error"] = false;
                $response["message"] = "Product deleted succesfully";
            } else {
                // User failed to delete
                $response["error"] = true;
                $response["message"] = "Product failed to delete. Please try again!";
            }
            echoRespnse(200, $response);
        });


$app->post('/get-product', function() use ($app) {

            verifyRequiredParams(array('ProductName'));

            $response = array();

            // reading post params
            $productId = $app->request->post('ProductName');
           
           
            $db = new DbHandler();
            $result = $db->getProduct($productId);

            $response["error"] = false;
            $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $result->fetch_assoc()) {
                $tmp = array();
                $tmp["ProductId"] = $task["ProductId"];
                $tmp["ProductName"] = $task["ProductName"];
                $tmp["image"] = $task["image"];
                $tmp["InStock"] = $task["InStock"];
                array_push($response["tasks"], $tmp);
            }

            echoRespnse(200, $response);
        });
		
/**
 * Verifying required params posted or not
 */
function verifyRequiredParams($required_fields) {
    $error = false;
    $error_fields = "";
    $request_params = array();
    $request_params = $_REQUEST;
    // Handling PUT request params
    if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
        $app = \Slim\Slim::getInstance();
        parse_str($app->request()->getBody(), $request_params);
    }
    foreach ($required_fields as $field) {
        if (!isset($request_params[$field]) || strlen(trim($request_params[$field])) <= 0) {
            $error = true;
            $error_fields .= $field . ', ';
        }
    }

    if ($error) {
        // Required field(s) are missing or empty
        // echo error json and stop the app
        $response = array();
        $app = \Slim\Slim::getInstance();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echoRespnse(400, $response);
        $app->stop();
    }
}

/**
 * Validating email address
 */
function validateEmail($email) {
    $app = \Slim\Slim::getInstance();
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $response["error"] = true;
        $response["message"] = 'Email address is not valid';
        echoRespnse(400, $response);
        $app->stop();
    }
}

/**
 * Echoing json response to client
 * @param String $status_code Http response code
 * @param Int $response Json response
 */
function echoRespnse($status_code, $response) {
    $app = \Slim\Slim::getInstance();
    // Http response code
    $app->status($status_code);

    // setting response content type to json
    $app->contentType('application/json');

    echo json_encode($response);
}

$app->run();
?>