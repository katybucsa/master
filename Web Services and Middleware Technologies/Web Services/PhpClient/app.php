<?php
require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/console/Console.php';

use PhpXmlRpc\Client;


$client = new Client("http://localhost:8069/");
$console = new Console($client);
$console->run();