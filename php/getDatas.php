<?php
	$con=mysqli_connect("localhost", "root", "2018", "feel_easy");
 
	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
 
	$uId = $_GET['uId'];

	$tables = array("light_on", "light_off", "fur_usage");
	for ($i = 0; $i < 3; $i++) {
		$result = mysqli_query($con, "SELECT date FROM $tables[$i] WHERE uId='$uId'");
		$row = mysqli_fetch_array($result);
		if (!is_null($row['date'])) {
			echo $row['date'];	
    		} else {
			echo '@';
		}
		if ($i != 2) {
			echo ',';
		}
	}
	echo "#";

	$result = mysqli_query($con, "SELECT * FROM recent_activity WHERE uId='$uId'");

	$fields = array('act1', 'act2', 'act3', 'act4', 'act5', 'act6');
	if ($row = mysqli_fetch_array($result) ) {
		for ($i = 0; $i < 6; $i++) {
			$field = $row[$fields[$i]];
			if (!empty($field)) {
				echo $field;
				echo ",";
			}
		}
	}
	
	mysqli_close($con);
?>