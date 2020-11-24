<?php
	$con = mysqli_connect("localhost", "root", "2018", "feel_easy");

	mysqli_set_charset($con, "utf8");

	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}

	$type = $_POST['type'];
	$name = $_POST['name'];
	$tel = $_POST['tel'];
	$fur = $_POST['fur'];
	$agree = $_POST['agree'];
	$related = $_POST['related'];

	if (strcmp($type, 'R')) {
		$result = mysqli_query($con, "SELECT date FROM user WHERE uId = '$related'");
		if ($row = mysqli_fetch_array($result)) {
			$date = $row['date'];
		}
	} else {
		date_default_timezone_set('Asia/Seoul');
		$date = date('Y-m-d H:i:s');
	}

	$result = mysqli_query($con, "INSERT INTO user(type, name, tel, uFur, agree, related, date) VALUES ('$type', '$name', '$tel', '$fur', '$agree', '$related', '$date')");
	if($result) {
		echo 'success';
		if (strcmp($type, 'P')) {
			$result = mysqli_query($con, "SELECT * FROM user WHERE name='$name' AND tel='$tel'");
			if ($row = mysqli_fetch_array($result)) {
				$uId = $row['uId'];
				$tables = array("light_on", "light_off", "fur_usage");
				for ($i = 0; $i < 3; $i++) {
					mysqli_query($con, "INSERT INTO $tables[$i](uId) VALUES ('$uId')");
				}
				mysqli_query($con, "INSERT INTO recent_activity(uId) VALUES ('$uId')");
				mysqli_query($con, "INSERT INTO warning(uId) VALUES ('$uId')");
			};
		}
	} else {
		echo 'failure';
	}

	mysqli_close($con);
?>