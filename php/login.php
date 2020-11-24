<?php
	$con=mysqli_connect("localhost", "root", "2018", "feel_easy");
 
	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
 
	$name = $_GET['name'];
	$tel = $_GET['tel'];
	$result = mysqli_query($con, "SELECT * FROM user WHERE name='$name' AND tel='$tel'");
	if ($row = mysqli_fetch_array($result)) {
		echo $row['uId'];
		echo ",";
		echo $row['type'];
		echo ",";
		echo $row['uFur'];
		echo ",";
		echo $row['agree'];
		echo ",";
		echo $row['related'];
		echo ",";
		echo $row['date'];
		echo ",";
		echo $row['name'];
	};

	mysqli_close($con);
?>