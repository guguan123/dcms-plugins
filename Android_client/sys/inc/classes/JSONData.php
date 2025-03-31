<?php

/**
 * Клас для ответа клиенту в виде JSON
 */

class JSONData {
	private static $instance;
	private static $type = false;

	const STATUS_OK = 1;
	const STATUS_ERROR = -1;

	private $status = self::STATUS_ERROR;
	private $json_data = array();
	private $error = null;

	public static function getInstance() {
		if (self::$instance === null) {
			self::$instance = new self;
		}
		return self::$instance;
	}

	private function __construct() { }

	public function setStatus($status) {
		$this -> status = $status;
		return $this;
	}

	public function setError($error) {
		$this -> error = $error;
		return $this;
	}

	public function setData($data) {
		$this -> json_data = $data;
		return $this;
	}

	public function getData() {
		return $this -> json_data;
	}

	public function setErrorAndHook($error) {
		$this -> setError($error) -> hook();
	}

	public function setDataAndHook($data) {
		$this -> setData($data) -> hook();
	}

	public function hook() {
		header("Content-type: application/json");
		if ($this -> error != null)
			$this -> json_data['error'] = $this -> error;
		$this -> json_data['status'] = $this -> status;
		echo json_encode($this -> json_data);
		exit();
	}
}
?>