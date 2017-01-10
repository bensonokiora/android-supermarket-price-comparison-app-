<?php 
 
 //Getting the page number which is to be displayed  

  //$id = $_GET['id']; 
  $name = $_GET['name']; 

 
 
 //Importing the database connection 
 require_once('dbConnect.php');
 

 $sql = "SELECT * from product where ProductName='$name' ";
 
 //Getting result 
 $result = mysqli_query($con,$sql); 
 
 //Adding results to an array 
 $res = array(); 
 
 
 while($row = mysqli_fetch_array($result)){
 array_push($res, array(
    "id"=>$row['ProductId'],

 "name"=>$row['ProductName'],
  "image"=>$row['image']
)
 );
 }
 //Displaying the array in json format 
 
 echo json_encode(array("result"=>$res));
 ?>