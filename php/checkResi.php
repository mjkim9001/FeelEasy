<?php
	$con=mysqli_connect("localhost", "root", "2018", "feel_easy");
 
	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
 
	$name = $_GET['name'];
	$tel = $_GET['tel'];

	$result = mysqli_query($con, "SELECT uId, uFur, agree FROM user WHERE name='$name' AND tel='$tel'");	
	if ($row = mysqli_fetch_array($result) ) {
		echo $row['uId'];
		echo ",";
		echo $row['uFur'];
		echo ",";
		echo $row['agree'];
	};
	
	mysqli_close($con);
?>