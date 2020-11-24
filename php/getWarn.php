<?php
	$con=mysqli_connect("localhost", "root", "2018", "feel_easy");
 
	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
 
	$uId = $_GET['uId'];
	$result = mysqli_query($con, "SELECT * FROM warning WHERE uId='$uId'");

	$fields = array('warn1', 'warn2', 'warn3', 'warn4', 'warn5');
	if ($row = mysqli_fetch_array($result) ) {
		for ($i = 0; $i < 5; $i++) {
			$field = $row[$fields[$i]];
			if (!empty($field)) {
				echo $field;
				echo ",";
			}
		}
	}
	
	mysqli_close($con);
?>