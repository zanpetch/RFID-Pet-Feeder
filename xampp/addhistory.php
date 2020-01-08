<?php

require_once './vendor/autoload.php';

use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;

$serviceAccount = ServiceAccount::fromJsonFile(__DIR__.'/secret/rfid-project-7a087-67f5f73ae003.json');

$firebase = (new Factory)
    ->withServiceAccount($serviceAccount)
    ->create();

$database = $firebase->getDatabase();

$petKey = $_GET["petKey"];
$type = $_GET["type"];
$eatWeight = $_GET["eatWeight"];

date_default_timezone_set("Asia/Bangkok");
$today = getdate();
$date = $today["mday"]."-".$today["mon"]."-".$today["year"];
$time = $today["hours"].":".$today["minutes"];

$newHistory = $database->getReference("historys/".$petKey."/".$date)
        ->push([
            'type' => $type,
            'eatWeight' => (int) $eatWeight,
            'time' => $time
        ]);
?>