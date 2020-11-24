<?php
	$con = mysqli_connect("localhost", "root", "2018", "feel_easy");

	mysqli_set_charset($con, "utf8");

	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}

	$table = $_GET['table'];
	$uId = $_GET['uId'];
	$content = $_GET['content'];
	
	date_default_timezone_set('Asia/Seoul');
	$date = date('Y-m-d H:i:s');
    	$daily = array('sun', 'mon', 'tue', 'wed', 'thur', 'fri', 'sat');
	$day = $daily[date('w')];

	$result = mysqli_query($con, "SELECT $day FROM $table WHERE uId = '$uId'");
	if ($row = mysqli_fetch_array($result)) {
		$count = $row[$day] + 1;
		$result = mysqli_query($con, "UPDATE $table SET $day = '$count', date = '$date' WHERE uId = '$uId'");
		if($result) {
			echo 'success';
		} else {
			echo 'failure';
		}
	}
	
	$content = date('m-d H:i')." ".$content;
	$fields = array('act1', 'act2', 'act3', 'act4', 'act5', 'act6');
	$result = mysqli_query($con, "SELECT position FROM recent_activity WHERE uId = '$uId'");
	if ($row = mysqli_fetch_array($result)) {
		$position = $row['position'];
		if ($position == 5) {
			$position = -1;
		}
		$field = $fields[$position + 1];
		echo $field;
		$result = mysqli_query($con, "UPDATE recent_activity SET $field = '$content', position = '$position'+1  WHERE uId = '$uId'");
		if ($result) {
			echo 'success';
		} else {
			echo 'failure';
		}
	}

	mysqli_close($con);
?>