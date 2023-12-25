package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
public class ResourcePool {

    public static Resource getOwnersItems_ItemEntities =
            new ClassPathResource("json/item-service-data/sbTest/getOwnersItems_itemEntity.json");
    public static Resource getOwnersItems_bookingEntities =
            new ClassPathResource("json/item-service-data/sbTest/getOwnersItems_bookingEntity.json");

    public static Resource getOwnersItems_commentEntities =
            new ClassPathResource("json/item-service-data/sbTest/getOwnersItems_commentEntity.json");

    public static Resource getOwnersItems_itemDtos =
            new ClassPathResource("json/item-service-data/sbTest/getOwnersItems_itemDto.json");

    public static Resource getItemById_itemEntity =
            new ClassPathResource("json/item-service-data/sbTest/getItemById_itemEntity.json");

    public static Resource getItemById_bookingEntities =
            new ClassPathResource("json/item-service-data/sbTest/getItemById_bookingEntity.json");

    public static Resource getItemById_commentEntities =
            new ClassPathResource("json/item-service-data/sbTest/getItemById_commentEntity.json");

    public static Resource getItemById_itemDto =
            new ClassPathResource("json/item-service-data/sbTest/getItemById_itemDto.json");

    public static Resource searchItems_itemEntity =
            new ClassPathResource("json/item-service-data/sbTest/searchItems_itemEntity.json");

    public static Resource searchItems_itemDto =
            new ClassPathResource("json/item-service-data/sbTest/searchItems_itemDto.json");

    public static Resource it_getItemById_bookingEntity =
            new ClassPathResource("json/item-service-data/itTest/getItemById_bookingEntity.json");

    public static Resource it_getItemById_commentEntity =
            new ClassPathResource("json/item-service-data/itTest/getItemById_commentEntity.json");

    public static Resource it_getOwnersItems_bookingEntity =
            new ClassPathResource("json/item-service-data/itTest/getOwnersItems_bookingEntity.json");

    public static Resource it_getOwnersItems_commentEntity =
            new ClassPathResource("json/item-service-data/itTest/getOwnersItems_commentEntity.json");

    public static Resource it_addComment_bookingEntity =
            new ClassPathResource("json/item-service-data/itTest/addComment_bookingEntity.json");

    public static Resource itemController_getItemById_itemDto =
            new ClassPathResource("json/item-controller-data/getItemById_itemDto.json");

    public static Resource itemController_getOwnersItems_itemDto =
            new ClassPathResource("json/item-controller-data/getOwnersItems_itemDto.json");

    public static Resource itemRequestService_sb_getUsersItemRequests_itemResponseDto =
            new ClassPathResource("json/item-request-service-data/sbTest/getUsersItemRequests_itemResponseDto.json");

    public static Resource itemRequestService_sb_getRequestById_itemResponseDto =
            new ClassPathResource("json/item-request-service-data/sbTest/getRequestById_itemResponseDto.json");

    public static Resource itemRequestService_it_getUsersItemRequests_itemRequestEntity =
            new ClassPathResource("json/item-request-service-data/itTest/getUsersItemRequests_itemRequestEntity.json");
    public static Resource itemRequestService_it_getUsersItemRequests_itemResponseDto =
            new ClassPathResource("json/item-request-service-data/itTest/getUsersItemRequests_itemResponseDto.json");

    public static Resource itemRequestService_it_getRequestById_itemResponseDto =
            new ClassPathResource("json/item-request-service-data/itTest/getRequestById_itemResponseDto.json");

    public static Resource itemRequestController_getUsersItemRequests_itemResponseDto =
            new ClassPathResource("json/item-request-controller-data/getUsersItemRequests_itemResponseDto.json");

    public static Resource itemRequestController_getRequestById_itemResponseDto =
            new ClassPathResource("json/item-request-controller-data/getRequestById_itemResponseDto.json");

    public static Resource bookingService_sb_createBooking_bookingDto =
            new ClassPathResource("json/booking-service-data/sbTest/createBooking_bookingDto.json");

    public static Resource bookingService_sb_getUsersBookings_bookingEntity_all =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingEntity_all.json");

    public static Resource bookingService_sb_getUsersBookings_bookingDto_all =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingDto_all.json");

    public static Resource bookingService_sb_getUsersBookings_bookingEntity_current =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingEntity_current.json");

    public static Resource bookingService_sb_getUsersBookings_bookingDto_current =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingDto_current.json");

    public static Resource bookingService_sb_getUsersBookings_bookingEntity_past =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingEntity_past.json");

    public static Resource bookingService_sb_getUsersBookings_bookingDto_past =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingDto_past.json");

    public static Resource bookingService_sb_getUsersBookings_bookingEntity_future =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingEntity_future.json");

    public static Resource bookingService_sb_getUsersBookings_bookingDto_future =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingDto_future.json");

    public static Resource bookingService_sb_getUsersBookings_bookingEntity_rejected =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingEntity_rejected.json");

    public static Resource bookingService_sb_getUsersBookings_bookingDto_rejected =
            new ClassPathResource("json/booking-service-data/sbTest/getUsersBookings_bookingDto_rejected.json");

    public static Resource bookingService_sb_getBooking_bookingEntity =
            new ClassPathResource("json/booking-service-data/sbTest/getBooking_bookingEntity.json");

    public static Resource bookingService_sb_getBooking_bookingDto =
            new ClassPathResource("json/booking-service-data/sbTest/getBooking_bookingDto.json");

    public static Resource bookingService_it_createBooking_bookingEntity =
            new ClassPathResource("json/booking-service-data/itTest/createBooking_bookingEntity.json");

    public static Resource bookingService_it_getUsersBookings_bookingDto =
            new ClassPathResource("json/booking-service-data/itTest/getUsersBookings_bookingDto.json");

    public static Resource bookingService_it_getBooking_bookingDto =
            new ClassPathResource("json/booking-service-data/itTest/getBooking_bookingDto.json");

    public static Resource bookingController_createBooking_bookingDto =
            new ClassPathResource("json/booking-service-data/itTest/getBooking_bookingDto.json");

    public static Resource bookingController_approveBooking_bookingDto =
            new ClassPathResource("json/booking-controller-data/approveBooking_bookingDto.json");

    public static Resource bookingController_getUsersBookings_bookingDto =
            new ClassPathResource("json/booking-controller-data/getUsersBookings_bookingDto.json");


    private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public static final String ERROR_IO = "Ошибка при получении данных из файла-ресурса";

    public static <T> T read(Resource resource, Class<T> objectClass) {
        try {
            return mapper.readValue(resource.getInputStream(), objectClass);
        } catch (IOException e) {
            log.error(ERROR_IO, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(Resource resource, TypeReference<T> tr) {
        try {
            return mapper.readValue(resource.getInputStream(), tr);
        } catch (IOException e) {
            log.error(ERROR_IO, e);
            throw new RuntimeException(e);
        }
    }
}
