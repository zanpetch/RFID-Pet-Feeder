<?php

require_once './vendor/autoload.php';

use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;

$tagID = $_GET["tagID"];

date_default_timezone_set("Asia/Bangkok");
$today = getdate();
$date = $today["mday"]."-".$today["mon"]."-".$today["year"];

$serviceAccount = ServiceAccount::fromJsonFile(__DIR__.'/secret/rfid-project-7a087-67f5f73ae003.json');

$firebase = (new Factory)
    ->withServiceAccount($serviceAccount)
    ->create();
$database = $firebase->getDatabase();

$refPets = $database->getReference('pets');

$pets = $refPets->getSnapshot()->getValue();

$keyPets = $refPets->getChildKeys();

$eatWeight = 0;
$eatType = "";
foreach($keyPets as $keyPet){
    if ($tagID == $pets[$keyPet]['tagID']){
        $eatWeight = $pets[$keyPet]['eatWeight'];
        $eatType = $pets[$keyPet]['eatType'];
        break;
    }
}

if($eatType == ""){
    $response['dataKey'] = '';
    $response['message'] = 'denied';
}else if ($eatType == "noLimit"){
    $response['dataKey'] = $keyPet;
    $response['message'] = 'eat';
}else{
    try{
        $refHistorys = $database->getReference('historys/'.$keyPet.'/'.$date);

        $historys = $refHistorys->getSnapshot()->getValue();

        $keyHistorys = $refHistorys->getChildKeys();

        $eatTotal = 0;
        foreach($keyHistorys as $keyHistory){
            $eatTotal += $historys[$keyHistory]['eatWeight'];
        }

        if ($eatWeight > $eatTotal){
            $response['dataKey'] = $keyPet;
            $response['message'] = 'eat';
        }else{
            $response['dataKey'] = $keyPet;
            $response['message'] = 'limit';
        }
    }catch(OutOfRangeException $e){
        $response['dataKey'] = $keyPet;
        $response['message'] = 'eat';
    }
}

header('Content-Type: application/json');
echo json_encode($response);
?>