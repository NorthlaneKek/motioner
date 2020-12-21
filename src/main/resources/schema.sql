create or replace function notify_new_alarm()
returns TRIGGER
language plpgsql
as $function$
    begin
        perform pg_notify('new_alarm_event', row_to_json(NEW)::text);
        return null;
    end;
$function$;

drop trigger if exists alarm_created on alarms;

create trigger alarm_created after insert on alarms
    for each row execute procedure notify_new_alarm();