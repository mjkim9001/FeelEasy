<?php
	$con=mysqli_connect("localhost", "root", "2018", "feel_easy");
 
	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}

	date_default_timezone_set('Asia/Seoul');
    	$daily = array('sun', 'mon', 'tue', 'wed', 'thur', 'fri', 'sat');
	$day = $daily[date('w')];
 
	$uId = $_GET['uId'];
	$result = mysqli_query($con, "(SELECT $day FROM light_on WHERE uId='$uId') 
			UNION ALL (SELECT $day FROM light_off WHERE uId='$uId') 
			UNION ALL (SELECT $day FROM fur_usage WHERE uId='$uId')");

	$fields = array('warn1', 'warn2', 'warn3', 'warn4', 'warn5');
	$i = 0;
	$count = 0;
	while ($row = mysqli_fetch_assoc($result)) {
		echo $row[$day];
		if ($row[$day] == 0) {
			$res = mysqli_query($con, "SELECT position FROM warning WHERE uId = '$uId'");
			if ($row = mysqli_fetch_array($res)) {
				$position = $row['position'];
				if ($position == 4) {
					$position = -1;
				}
				$field = $fields[$position + 1];
				$content = date('m-d H:i')."#light";
				if ($i == 0) {
					$count++;
				}
				if ($i == 1 && $count == 1) {
					$i++;
					continue;
				}
				if ($i == 2) {
					$content = date('m-d H:i')."#fur";
				}
				$res = mysqli_query($con, "UPDATE warning SET $field = '$content', position = '$position'+1  WHERE uId = '$uId'");
				if ($res) {
					echo 'success';
				}
			}
		}
		$i++;
    	}
	
	mysqli_close($con);
?>