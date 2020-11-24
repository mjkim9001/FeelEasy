<?php
	$con=mysqli_connect("localhost", "root", "2018", "feel_easy");
 
	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
 
	$uId = $_GET['uId'];

	$tables = array("light_on", "fur_usage");
	for ($i = 0; $i < 2; $i++) {
		$result = mysqli_query($con, "SELECT * FROM $tables[$i] WHERE uId='$uId'");
		if ($row = mysqli_fetch_array($result) ) {
			echo $row['sun'];
			echo ",";
			echo $row['mon'];
			echo ",";
			echo $row['tue'];
			echo ",";
			echo $row['wed'];
			echo ",";
			echo $row['thur'];
			echo ",";
			echo $row['fri'];
			echo ",";
			echo $row['sat'];
		};
		echo " ";
	}
	
	mysqli_close($con);
?>