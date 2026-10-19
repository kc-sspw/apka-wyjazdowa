import me.havooc.poligon.controller.ScheduleController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import me.havooc.poligon.model.ScheduleDay;
import me.havooc.poligon.model.TrainingPath;
import me.havooc.poligon.repository.ScheduleDayRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest {

    @Mock
    private ScheduleDayRepository scheduleDayRepository;

    @InjectMocks
    private ScheduleController scheduleController;

    @Test
    public void shouldReturnNotModified_WhenDataIsNotChanged() {
        // Given - Klient ma aktualne dane (brak zmian po podanej dacie)
        LocalDateTime clientTimestamp = LocalDateTime.now();
        when(scheduleDayRepository.existsUpdatedAfter(clientTimestamp))
                .thenReturn(false);
        // When - Wywołanie endpointu z parametrem czasowym
        ResponseEntity<List<ScheduleDay>> response =
                scheduleController.getByPath(TrainingPath.LIDERSKA, clientTimestamp);
        // Then - Serwer powinien zwrócić 304 (bez danych)
        assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode());
        verify(scheduleDayRepository, times(0)).findBySchedulePath(any());
    }
    @Test
    public void shouldReturnData_WhenChangesDetected() {
        // Given - W bazie pojawiły się nowsze dane
        LocalDateTime clientTimestamp = LocalDateTime.now().minusHours(1);
        when(scheduleDayRepository.existsUpdatedAfter(clientTimestamp))
                .thenReturn(true);
        when(scheduleDayRepository.findBySchedulePath(any()))
                .thenReturn(new ArrayList<>(List.of(new ScheduleDay())));
        // When
        ResponseEntity<List<ScheduleDay>> response =
                scheduleController.getByPath(TrainingPath.LIDERSKA, clientTimestamp);
        // Then - Serwer zwraca 200 OK i listę
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}