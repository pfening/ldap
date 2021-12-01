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

<div class="jumbotron">
  <h2 class="display-2">Welcome ${user} to DHL LDAP Search</h2>

    <form class="form-inline" method="post" action="/login">
      <div class="form-group">
      <p>Please type your DHL LDAP Password, you need to type it only once.</p>
      <p>The application will memorize it, until you stop the application.</p>
      <div class="form-group">
        <label for="pwd">Password</label>
        <input type="password" 
                    class="form-control" 
                    id="pwd" 
                    name="pwd"
                    placeholder="LDAP Password">
      </div>
      <button type="submit" class="btn btn-primary">Submit</button>
    </form>
</div>

</body>
</html>