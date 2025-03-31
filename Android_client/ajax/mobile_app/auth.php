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

if (!isset($_POST['nick']) || !isset($_POST['password']))
	$jsonData -> setErrorAndHook(MobileAppApi::$errors[2]);

$app = new MobileAppApi();
$app -> setNick($_POST['nick']);
$app -> setPassword($_POST['password']);

if (!$app -> checkUser())
	$jsonData -> setErrorAndHook(MobileAppApi::$errors[1]);

$jsonData -> setStatus(JSONData::STATUS_OK);
$user = $app -> getCurrentUser();
$user['token'] = $app -> getToken();
$jsonData -> setDataAndHook(array(
	'user' => $user
	// 'counters' => $app -> getEventsCount(), 
	// 'contents' => $app -> getEventsContent()
));