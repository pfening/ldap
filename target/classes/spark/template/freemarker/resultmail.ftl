<html>
<head>
<title>DHL LDAP Search</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<script src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-3.1.0.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>

<nav class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">

    <div class="navbar-header">
      <a class="navbar-brand" href="/">DHL LDAP Search</a>
    </div>

    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav">
        <li><a href="http://localhost:4567/searchid">Search by ID</a></li>
      </ul>
      <ul class="nav navbar-nav">
        <li><a href="http://localhost:4567/searchmail">Search by Email</a></li>
      </ul>
      <ul class="nav navbar-nav">
        <li><a href="http://localhost:4567/upload">CSV Batch upload (mail)</a></li>
      </ul>

    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
<br><br>
<div class="col-md-8">
<h3>The details of ${mail} email</h3>
<p>Display name: ${name}</p>
<p>User id: ${cn}</p>
<p>Family name: ${sn}</p>
<p>Name: ${gname}</p>
<p>Email: ${mail}</p>
<p>Manager uid: ${manager}</p>
<p>Country: ${country}</p>
<p>Employee type: ${type}</p>

</div>

</body>
</html>