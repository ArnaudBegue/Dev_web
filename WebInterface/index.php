<!DOCTYPE html>
<html>
	<head>
		<title>Enzyme Descriptor</title>
		<meta charset="utf-8">
		<link rel="stylesheet" type="text/css" href="style.css">
	</head>
	<body>
		<?php
			$a="Type your query here !"
		?>
		<div class="center title"><h1>Enzyme Descriptor</h1></div>
		
		<div class="center addBox">
			<form action="<?php echo $_SERVER['PHP_SELF'];?>" method="post">
				<input type="text" name="query" value="<?php echo $a?>">
				<select>
					<option> All
					<option> EC
					<option> Enz Name
					<option> Function
					<option> Author
				</select>
				<input type="submit" name="Submit">
			</form>
		</div>
		
	</body>
</html>
