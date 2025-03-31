<?php

/**
 * MobileAppApi
 * 客户端应用程序响应类
 */

class MobileAppApi {
    public static $errors = array(
        1 => '授权错误', 
        2 => '缺少参数'
    );
    protected $nick, 
              $password, 
              $token, 
              $id, 
              $user, 
              $events_count = array();

    public function __construct() {}

    /**
     * 设置用户名
     * @param string $nick 用户名
     * @return $this
     */

    public function setNick($nick) {
        $this -> nick = my_esc($nick);
        return $this;
    }

    /**
     * 设置用户密码
     * @param string $password 用户密码
     * @return $this
     */

    public function setPassword($password) {
        $this -> password = my_esc($password);
        return $this;
    }

    /**
     * 设置用户令牌
     * @param string $token 用户令牌
     * @return $this
     */

    public function setToken($token) {
        $this -> token = my_esc($token);
        return $this;
    }

    /**
     * 设置用户ID
     * @param string $id 用户ID
     * @return $this
     */

    public function setID($id) {
        $this -> id = intval($id);
        return $this;
    }

    /**
     * 检查用户授权数据
     * @return boolean
     */

    public function checkUser() {
        $this -> user = dbassoc(dbquery("SELECT * FROM `user` WHERE `nick` = '{$this -> nick}' AND `pass` = '" . $this -> getCryptedPassword() . "'"));
        return @$this -> user['id'] != 0;
    }

    /**
     * 检查令牌和ID
     * @return boolean
     */

    public function checkTokenAndID() {
        $this -> user = dbassoc(dbquery("SELECT * FROM `user` WHERE `mobile_app_token` = '{$this -> token}' AND `id` = '{$this -> id}'"));
        return @$this -> user['id'] != 0;
    }

    /**
     * 返回加密的密码
     * @return string
     */

    public function getCryptedPassword() {
        include_once(H . 'sys/inc/shif.php');
        return shif($this -> password);
    }

    /**
     * 返回令牌
     * @return string
     */

    public function getToken() {
        include_once(H . 'sys/inc/shif.php');

        // 如果令牌为空
        if (!$this -> user['mobile_app_token'])
            $this -> generateToken();

        return $this -> user['mobile_app_token'];
    }

    /**
     * 生成令牌
     * @return $this
     */

    public function generateToken() {
        $user = $this -> user;
        include_once(H . 'sys/inc/shif.php');
        $token = shif($this -> user['password'] . $this -> user['nick'] . 'mobile_app_token' . microtime(true));
        dbquery("UPDATE `user` SET `mobile_app_token` = '$token' WHERE `id` = '$user[id]'");
        $this -> user = get_user($user['id']);
        return $this;
    }

    /**
     * 返回新事件的数组
     * @return array
     */

    public function getEventsCount() {
        $user = $this -> user;
        if (empty($this -> events_count))
            $this -> events_count = array(
                'mail' => dbresult(dbquery("SELECT COUNT(`mail`.`id`) FROM `mail` LEFT JOIN `users_konts` ON `mail`.`id_user` = `users_konts`.`id_kont` AND `users_konts`.`id_user` = '$user[id]' WHERE `mail`.`id_kont` = '$user[id]' AND (`users_konts`.`type` IS NULL OR `users_konts`.`type` = 'common' OR `users_konts`.`type` = 'favorite') AND `mail`.`read` = '0'"), 0), 
                'discussions' => dbresult(dbquery("SELECT COUNT(`count`) FROM `discussions` WHERE `id_user` = '$user[id]' AND `count` > '0' "), 0), 
                'notification' => dbresult(dbquery("SELECT COUNT(`read`) FROM `notification` WHERE `id_user` = '$user[id]' AND `read` = '0'"), 0), 
                'tape' => dbresult(dbquery("SELECT COUNT(`read`) FROM `tape` WHERE `id_user` = '$user[id]' AND `read` = '0' "), 0), 
                'friends' => dbresult(dbquery("SELECT COUNT(id) FROM `frends_new` WHERE `to` = '$user[id]'"), 0), 
                'guests' => dbresult(dbquery("SELECT COUNT(*) FROM `my_guests` WHERE `id_ank` = '$user[id]' AND `read` = '1'"), 0)
            );
        return $this -> events_count;
    }

    /**
     * 返回新事件的内容
     * @return array
     */

    public function getEventsContent() {
        $user = $this -> user;
        $events_count = $this -> getEventsCount();

        $events_content = array(
            'mail' => array(), 
            'friends' => array(), 
            'guests' => array()
        );

        if ($events_count['mail'] > 0) {
            $messages = dbquery("SELECT `mail`.*, COUNT(`mail`.`id`) AS `count` FROM `mail` LEFT JOIN `users_konts` ON `mail`.`id_user` = `users_konts`.`id_kонт` AND `users_kонтs`.`id_user` = '$user[id]' WHERE `mail`.`id_kонт` = '$user[id]' AND (`users_kонтs`.`type` IS NULL OR `users_kонтs`.`type` = 'common' OR `users_kонтs`.`type` = 'favorite') AND `mail`.`read` = '0' AND `mail`.`id` = (SELECT `id` FROM `mail` AS `mail_new` WHERE `mail_new`.`id_user` = `mail`.`id_user` AND `mail_new`.`id_kонт` = '$user[id]' ORDER BY `mail_new`.`time` DESC LIMIT 1) GROUP BY `mail`.`id_user` ORDER BY `mail`.`time` ASC, `count` ASC");
            while ($message = dbassoc($messages)) {
                $message_user = self::getUser($message['id_user']);

                $events_content['mail'][] = array(
                    'id' => $message['id'], 
                    'user' => $message_user, 
                    'message' => $message['msg'], 
                    'time' => vremja($message['time'])
                );
            }
        }

        if ($events_count['friends'] > 0) {
            $new_friends = dbquery("SELECT * FROM `frends_new` WHERE `to` = $user[id] ORDER BY `time`");
            while ($new_friend = dbassoc($new_friends)) {
                $friend_user = self::getUser($new_friend['user']);

                $events_content['friends'][] = array(
                    'id' => $new_friend['id'], 
                    'user' => $friend_user, 
                    'time' => vremja($new_friend['time'])
                );
            }
        }

        if ($events_count['guests'] > 0) {
            $new_guests = dbquery("SELECT * FROM `my_guests` WHERE `id_ank` = '$user[id]' AND `read` = '1' ORDER BY `time` DESC");
            while ($new_guest = dbassoc($new_guests)) {
                $guest_user = self::getUser($new_guest['id_user']);

                $events_content['guests'][] = array(
                    'id' => $new_guest['id'], 
                    'user' => $guest_user, 
                    'time' => vremja($new_guest['time'])
                );
            }
        }

        return $events_content;
    }

    /**
     * 返回用户数据数组
     * @param $user_id 用户ID
     * @return array
     */

    public static function getUser($user_id) {
        $user = get_user($user_id);

        return array(
            'id' => $user['id'], 
            'nick' => $user['nick'], 
            'avatar' => self::getAvatar($user['id'])
        );
    }

    /**
     * 返回当前用户的数据数组
     * @return array
     */

    public function getCurrentUser() {
        return self::getUser($this -> user['id']);
    }

    /**
     * 返回用户头像链接
     * @param $user_id 用户ID
     * @return array
     */

    public static function getAvatar($user_id) {
        $user_id = intval($user_id);
        $result = array('exists' => false);

        $sql_query = dbquery("SELECT * FROM `gallery_foto` WHERE `id_user` = $user_id AND `avatar` = '1' LIMIT 1");
        if (dbdbrows($sql_query)) {
            $avatar = dbarray($sql_query);
            if (is_file(H . "sys/gallery/48/$avatar[id].$avatar[ras]")) {
                $result['exists'] = true;
                $result['id'] = $avatar['id'];
                $result['extension'] = $avatar['ras'];
            }
        }

        return $result;
    }
}
?>