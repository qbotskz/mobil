/*UPDATE admin SET comment=users.user_name+' - ' + users.phone
FROM users WHERE users.chat_id=admin.chat_id;*/
/*select *
from admin a
  inner join users u
    on a.chat_id = u.chat_id
where a.chat_id=u.chat_id;*/

/*
update admin
set a.comment = u.user_name + '- ' + u.phone
from admin a
  inner join users u
    on a.chat_id = u.chat_id
where  a.chat_id = u.chat_id*/

/*SELECT
    task.id AS id_task,
  *
FROM task
  INNER JOIN users ON task.tenant_id = users.chat_id
  LEFT JOIN centres ON task.id_centres = centres.id
  LEFT JOIN position ON task.id_position = position.id
  LEFT JOIN status ON task.id_status = status.id
WHERE task.dataadd BETWEEN TO_TIMESTAMP('2018-03-06 00:0:00.000000000', 'YYYY-MM-DD HH24:MI:SS.FF') AND TO_TIMESTAMP(
    '2018-04-08 00:0:00.000000000', 'YYYY-MM-DD HH24:MI:SS.FF') ORDER BY task.id*/
-- 2018-03-06 15:06:00.000000
-- 2018-04-08 15:06:00.000000