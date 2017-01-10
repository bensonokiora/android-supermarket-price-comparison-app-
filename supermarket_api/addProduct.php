<?php 
	if($_SERVER['REQUEST_METHOD']=='POST'){
		
		//Getting values
		$ProductName = $_POST['name'];
		$ProductPrice = $_POST['price'];
		$Description = $_POST['description'];
		$image = $_POST['image'];
		$supermarket = $_POST['supermarket'];

		
		$sql ="SELECT ProductId FROM products ORDER BY ProductId ASC";
			require_once('dbConnect.php');

		$res = mysqli_query($con,$sql);
		
		$id = 0;
		
		while($row = mysqli_fetch_array($res)){
				$id = $row['ProductId'];
		} 
		$path = "uploads/$id.png";
		
		$actualpath = "http://192.168.137.1/supermarket_api/$path";
		
		
		
	
		//Creating an sql query
		$sql = "INSERT INTO products (ProductName,ProductPrice,Description,supermarket,image) VALUES ('$ProductName','$ProductPrice','$Description','$supermarket','$actualpath')";
		
		//Executing query to database
		if(mysqli_query($con,$sql)){
			file_put_contents($path,base64_decode($image));

			echo 'Product Added Successfully';
		}else{
			echo 'Could Not Add Product'. mysqli_error($con) ;
		}
		
		//Closing the database 
		mysqli_close($con);
	}