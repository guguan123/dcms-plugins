<?php
// 从文件夹"sys/fnc"加载其余功能 
$opdirbase = opendir(__DIR__ . '/../fnc');
while ($filebase = readdir($opdirbase)) {
	if (preg_match('#\.php$#i', $filebase)) {
		include_once(__DIR__ . '/../fnc/' . $filebase);
	}
}