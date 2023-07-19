package click.klaassen.calsync.write;

import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.calendar.model.Event;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportBatchCallback extends JsonBatchCallback<Event> {

        @Override
        public void onFailure(GoogleJsonError googleJsonError, HttpHeaders httpHeaders) {
            log.error(googleJsonError.getMessage());
        }

        @Override
        public void onSuccess(Event event, HttpHeaders httpHeaders) {
            log.debug("Insert batch executed successfully [{}]", event.getSummary());
        }

}

