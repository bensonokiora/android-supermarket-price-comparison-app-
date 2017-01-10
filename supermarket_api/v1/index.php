<?php

require_once '../include/DbHandler.php';
require_once '../include/PassHash.php';
require '.././libs/Slim/Slim.php';

\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();

// User id from db - Global Variable
$user_id = NULL;

/**
 * Adding Middle Layer to authenticate every request
 * Checking if the request has valid api key in the 'Authorization' header
 */
function authenticate(\Slim\Route $route) {
    // Getting request headers
    $headers = apache_request_headers();
    $response = array();
    $app = \Slim\Slim::getInstance();

    // Verifying Authorization Header
    if (isset($headers['Authorization'])) {
        $db = new DbHandler();

        // get the api key
        $api_key = $headers['Authorization'];
        // validating api key
        if (!$db->isValidApiKey($api_key)) {
            // api key is not present in users table
            $response["error"] = true;
            $response["message"] = "Access Denied. Invalid Api key";
            echoRespnse(401, $response);
            $app->stop();
        } else {
            global $user_id;
            // get user primary key id
            $user_id = $db->getUserId($api_key);
        }
    } else {
        // api key is missing in header
        $response["error"] = true;
        $response["message"] = "Api key is misssing";
        echoRespnse(400, $response);
        $app->stop();
    }
}

/**
 * ----------- METHODS WITHOUT AUTHENTICATION ---------------------------------
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
                    $response['apiKey'] = $user['api_key'];
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

/*
 * ------------------------ METHODS WITH AUTHENTICATION ------------------------
 */

/**
 * Listing all tasks of particual user
 * method GET
 * url /tasks          
 */
$app->get('/tasks', 'authenticate', function() {
            global $user_id;
            $response = array();
            $db = new DbHandler();

            // fetching all user tasks
            $result = $db->getAllUserTasks($user_id);

            $response["error"] = false;
            $response["tasks"] = array();

            // looping through result and preparing tasks array
            while ($task = $result->fetch_assoc()) {
                $tmp = array();
                $tmp["id"] = $task["id"];
                $tmp["task"] = $task["task"];
                $tmp["status"] = $task["status"];
                $tmp["createdAt"] = $task["created_at"];
                array_push($response["tasks"], $tmp);
            }

            echoRespnse(200, $response);
        });

/**
 * Listing single task of particual user
 * method GET
 * url /tasks/:id
 * Will return 404 if the task doesn't belongs to user
 */
$app->get('/tasks/:id', 'authenticate', function($task_id) {
            global $user_id;
            $response = array();
            $db = new DbHandler();

            // fetch task
            $result = $db->getTask($task_id, $user_id);

            if ($result != NULL) {
                $response["error"] = false;
                $response["id"] = $result["id"];
                $response["task"] = $result["task"];
                $response["status"] = $result["status"];
                $response["createdAt"] = $result["created_at"];
                echoRespnse(200, $response);
            } else {
                $response["error"] = true;
                $response["message"] = "The requested resource doesn't exists";
                echoRespnse(404, $response);
            }
        });
/**
 * Listing single task of particual user
 * method GET
 * url /tasks/:id
 * Will return 404 if the task doesn't belongs to user
 */
 /**
 * Listing single task of particual user
 * method GET
 * url /tasks/:id
 * Will return 404 if the task doesn't belongs to user
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
/**
 * Creating new task in db
 * method POST
 * params - name
 * url - /tasks/
 */
$app->post('/tasks', 'authenticate', function() use ($app) {
            // check for required params
            verifyRequiredParams(array('task'));

            $response = array();
            $task = $app->request->post('task');
            global $user_id;
            $db = new DbHandler();

            // creating new task
            $task_id = $db->createTask($user_id, $task);

            if ($task_id != NULL) {
                $response["error"] = false;
                $response["message"] = "Task created successfully";
                $response["task_id"] = $task_id;
                echoRespnse(201, $response);
            } else {
                $response["error"] = true;
                $response["message"] = "Failed to create task. Please try again";
                echoRespnse(200, $response);
            }            
        });

/**
 * Updating existing task
 * method PUT
 * params task, status
 * url - /tasks/:id
 */
$app->put('/tasks/:id', 'authenticate', function($task_id) use($app) {
            // check for required params
            verifyRequiredParams(array('task', 'status'));

            global $user_id;            
            $task = $app->request->put('task');
            $status = $app->request->put('status');

            $db = new DbHandler();
            $response = array();

            // updating task
            $result = $db->updateTask($user_id, $task_id, $task, $status);
            if ($result) {
                // task updated successfully
                $response["error"] = false;
                $response["message"] = "Task updated successfully";
            } else {
                // task failed to update
                $response["error"] = true;
                $response["message"] = "Task failed to update. Please try again!";
            }
            echoRespnse(200, $response);
        });

/**
 * Deleting task. Users can delete only their tasks
 * method DELETE
 * url /tasks
 */
$app->delete('/tasks/:id', 'authenticate', function($task_id) use($app) {
            global $user_id;

            $db = new DbHandler();
            $response = array();
            $result = $db->deleteTask($user_id, $task_id);
            if ($result) {
                // task deleted successfully
                $response["error"] = false;
                $response["message"] = "Task deleted succesfully";
            } else {
                // task failed to delete
                $response["error"] = true;
                $response["message"] = "Task failed to delete. Please try again!";
            }
            echoRespnse(200, $response);
        });

/**
* Confirm payment
*/
$app->post('/mpesa-ref', function() use ($app) {
            // check for required params $fname,$mname,$lname,$krapin,$krapass, $email, $phone,$mpesaref) {

            verifyRequiredParams(array('mpesaref'));

            $response = array();

            // reading post params
             $mpesaref = $app->request->post('mpesaref');

           
            $db = new DbHandler();
            $res = $db->checkMpesaRef($mpesaref);

            if ($res == MPESAREF_ALREADY_EXISTED) {
                $response["error"] = false;
                $response["message"] = "payments successfully made";
            } else if ($res == MPESAREF_NOT_EXISTED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while validating payment";
            }
            // echo json response
            echoRespnse(201, $response);
        });

$app->post('/test-count', function() use ($app) {
 $response = array();
 $db = new DbHandler();
            $result = $db->testcount();

            if ($result != NULL) {
                $response["error"] = false;
                $response["id"] = $result;

               echoRespnse(200, $response);
            } else {
                $response["error"] = true;
                $response["message"] = "The requested resource doesn't exists";
                echoRespnse(404, $response);
            }
   });
/**
* save registration form
*/
$app->post('/save-user', function() use ($app) {
            // check for required params $fname,$mname,$lname,$krapin,$krapass, $email, $phone,$mpesaref) {

            verifyRequiredParams(array('name','email', 'phone','token'));

            $response = array();

            // reading post params
            $username = $app->request->post('name');
            $email = $app->request->post('email');
            $phone = $app->request->post('phone');
            $token = $app->request->post('token');

            // validating email address
            validateEmail($email);

            $db = new DbHandler();
            $res = $db->saveUser($username, $email, $phone, $token);

            if ($res == FORM_SAVED_SUCCESSFULLY) {
                $response["error"] = false;
                $response["message"] = "details successfully uploaded";
            } else if ($res == FORM_SAVED_FAILED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while uploading data";
            }
            // echo json response
            echoRespnse(201, $response);
        });
/**
* save registration form
*/

$app->post('/get-product', function() use ($app) {
            // check for required params $fname,$mname,$lname,$krapin,$krapass, $email, $phone,$mpesaref) {

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
		
		
$app->post('/reg-form', function() use ($app) {
            // check for required params $fname,$mname,$lname,$krapin,$krapass, $email, $phone,$mpesaref) {

            verifyRequiredParams(array('fname','mname','lname','email', 'phone','mpesaref'));

            $response = array();

            // reading post params
            $fname = $app->request->post('fname');
            $mname = $app->request->post('mname'); 
            $lname = $app->request->post('lname'); 
            $email = $app->request->post('email');
            $phone = $app->request->post('phone');
            $mpesaref = $app->request->post('mpesaref');

            // validating email address
            validateEmail($email);

            $db = new DbHandler();
            $res = $db->createRegForm($fname, $mname, $lname, $email, $phone, $mpesaref);

            if ($res == FORM_SAVED_SUCCESSFULLY) {
                $response["error"] = false;
                $response["message"] = "details successfully uploaded";
            } else if ($res == FORM_SAVED_FAILED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while uploading data";
            }
            // echo json response
            echoRespnse(201, $response);
        });
/**
* save nil form
*/
$app->post('/nil-form', function() use ($app) {
            // check for required params $fname,$mname,$lname,$krapin,$krapass, $email, $phone,$mpesaref) {

            verifyRequiredParams(array('fname','mname','lname','krapin','krapass', 'email', 'phone','mpesaref'));

            $response = array();

            // reading post params
            $fname = $app->request->post('fname');
            $mname = $app->request->post('mname'); 
            $lname = $app->request->post('lname'); 
            $krapin = $app->request->post('krapin');
            $krapass = $app->request->post('krapass');
            $email = $app->request->post('email');
            $phone = $app->request->post('phone');
            $mpesaref = $app->request->post('mpesaref');

            // validating email address
            validateEmail($email);

            $db = new DbHandler();
            $res = $db->createNilForm($fname, $mname, $lname, $krapin, $krapass, $email, $phone, $mpesaref);

            if ($res == FORM_SAVED_SUCCESSFULLY) {
                $response["error"] = false;
                $response["message"] = "details successfully uploaded";
            } else if ($res == FORM_SAVED_FAILED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while uploading data";
            }
            // echo json response
            echoRespnse(201, $response);
        });

/**
* save nil form
*/
$app->post('/paye-form', function() use ($app) {
            // check for required params $fname,$mname,$lname,$krapin,$krapass, $email, $phone,$mpesaref) {

            verifyRequiredParams(array('fname','mname','lname','krapin','krapass', 'email', 'phone','mpesaref'));

            $response = array();

            // reading post params
            $fname = $app->request->post('fname');
            $mname = $app->request->post('mname'); 
            $lname = $app->request->post('lname'); 
            $krapin = $app->request->post('krapin');
            $krapass = $app->request->post('krapass');
            $email = $app->request->post('email');
            $phone = $app->request->post('phone');
            $mpesaref = $app->request->post('mpesaref');
            $attachment = $app->request->post('attachment');

            // validating email address
            validateEmail($email);

            $db = new DbHandler();
            $res = $db->createPayeForm($fname, $mname, $lname, $krapin, $krapass, $email, $phone, $mpesaref, $attachment);

            if ($res == FORM_SAVED_SUCCESSFULLY) {
                $response["error"] = false;
                $response["message"] = "details successfully uploaded";
            } else if ($res == FORM_SAVED_FAILED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while uploading data";
            }
            // echo json response
            echoRespnse(201, $response);
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