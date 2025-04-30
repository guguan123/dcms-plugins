<?php
echo $db->query(
	"SELECT COUNT(DISTINCT ul.id_user) AS online_users
	FROM `user_log` ul
	WHERE ul.last_online > NOW() - INTERVAL 100 SECOND
		AND ul.ban = 0
		AND ul.url LIKE '/plugins/farm/%'
		AND ul.last_online = (
			SELECT MAX(last_online)
			FROM `user_log` ul2
			WHERE ul2.id_user = ul.id_user
				AND ul2.last_online > NOW() - INTERVAL 100 SECOND
				AND ul2.ban = 0
		)
")['online_users'].' 人';