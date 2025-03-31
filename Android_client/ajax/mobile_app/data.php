<?php
include_once '../../sys/inc/start.php';
include_once '../../sys/inc/compress.php';
include_once '../../sys/inc/sess.php';
include_once '../../sys/inc/home.php';
include_once '../../sys/inc/settings.php';
include_once '../../sys/inc/db_connect.php';
include_once '../../sys/inc/ipua.php';
include_once '../../sys/inc/fnc.php';

include_once(H . 'sys/inc/classes/MobileAppApi.php');
include_once(H . 'sys/inc/classes/JSONData.php');

$jsonData = JSONData::getInstance();

if (!isset($_POST['token']) || !isset($_POST['id']))
	$jsonData -> setErrorAndHook(MobileAppApi::$errors[2]);

$app = new MobileAppApi();
$app -> setToken($_POST['token']);
$app -> setID($_POST['id']);

if (!$app -> checkTokenAndID())
	$jsonData -> setErrorAndHook(MobileAppApi::$errors[1]);

$jsonData -> setStatus(JSONData::STATUS_OK);
$jsonData -> setDataAndHook(array(
	'user' => $app -> getCurrentUser(), 
	'counters' => $app -> getEventsCount(), 
	'contents' => $app -> getEventsContent()
));