package petTopia.service.vendor;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import petTopia.dto.vendor.FriendlyShopDto;
import petTopia.dto.vendor.request.AddFriendlyShopRequest;
import petTopia.dto.vendor.request.ModifyFriendlyShopRequest;
import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.FriendlyShopRepository;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorRepository;

import static petTopia.dto.vendor.FriendlyShopDto.fromEntity;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class FriendlyShopService {
    private static final String GOOGLE_MAPS_API_KEY = "AIzaSyAdtvNzj4RCUhcxxFuXDpvjXCglqPja6cI";
    private static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private final FriendlyShopRepository friendlyShopRepository;
    private final VendorRepository vendorRepository;
    private final VendorCategoryRepository vendorCategoryRepository;

    /* 透過 Google API 取得地址之經緯度 */
    public BigDecimal[] getLatLng(String address) {
        if (StringUtil.isBlank(address)) {
            log.warn("Address is blank");
            return null;
        }

        String urlString = UriComponentsBuilder.fromUriString(GOOGLE_MAPS_API_URL)
                .queryParam("address", address)
                .queryParam("key", GOOGLE_MAPS_API_KEY)
                .toUriString();

        HttpURLConnection connection = null;

        try {
            URL url = new URI(urlString).toURL();

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (InputStream inputStream = connection.getInputStream();) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(inputStream);

                JsonNode results = jsonNode.path("results");
                if (results.isArray() && !results.isEmpty()) {
                    JsonNode location = results.get(0)
                            .path("geometry")
                            .path("location");

                    if (!location.isMissingNode()) {
                        double lat = location.path("lat").asDouble();
                        double lng = location.path("lng").asDouble();

                        return new BigDecimal[]{BigDecimal.valueOf(lat), BigDecimal.valueOf(lng)};
                    }

                }
            }

            log.warn("Longitude and latitude of {} not found", address);
            return null;
        } catch (Exception e) {
            log.error("Get longitude and latitude of {} failed", address, e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Transactional
    public FriendlyShopDto findFirstByVendorId(Integer vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        FriendlyShop friendlyShop = friendlyShopRepository.findFirstByVendor(vendor).orElse(null);

        if (friendlyShop == null) {
            AddFriendlyShopRequest request = AddFriendlyShopRequest.builder()
                    .name(vendor.getName())
                    .address(vendor.getAddress())
                    .categoryId(vendor.getVendorCategory().getId())
                    .build();

            return addFriendlyShop(request);
        }

        friendlyShop.setName(vendor.getName());
        friendlyShop.setVendorCategory(vendor.getVendorCategory());
        friendlyShop.setAddress(vendor.getAddress());

        BigDecimal[] latLng = getLatLng(vendor.getAddress());
        if (latLng != null && latLng.length == 2) {
            friendlyShop.setLatitude(latLng[0]);
            friendlyShop.setLongitude(latLng[1]);
        }

        FriendlyShop savedFriendlyShop = friendlyShopRepository.save(friendlyShop);

        return fromEntity(savedFriendlyShop);
    }

    public List<FriendlyShopDto> findAll() {
        return friendlyShopRepository.findAll()
                .stream()
                .map(FriendlyShopDto::fromEntity)
                .toList();
    }

    public List<FriendlyShopDto> findByKeyword(String keyword) {
        return friendlyShopRepository.findByNameContaining(keyword)
                .stream()
                .map(FriendlyShopDto::fromEntity)
                .toList();
    }

    public List<FriendlyShopDto> findByVendorId(Integer vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        return friendlyShopRepository.findByVendor(vendor)
                .stream()
                .map(FriendlyShopDto::fromEntity)
                .toList();
    }

    public List<FriendlyShopDto> findByCategoryId(Integer categoryId) {
        VendorCategory category = vendorCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor category not found"));

        return friendlyShopRepository.findByVendorCategory(category)
                .stream()
                .map(FriendlyShopDto::fromEntity)
                .toList();
    }

    /* 新增友善店家 */
    @Transactional
    public FriendlyShopDto addFriendlyShop(AddFriendlyShopRequest request) {
        VendorCategory category = vendorCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Vendor category not found"));

        FriendlyShop friendlyShop = new FriendlyShop();
        friendlyShop.setName(request.getName());
        friendlyShop.setAddress(request.getAddress());
        friendlyShop.setVendorCategory(category);

        BigDecimal[] latLng = getLatLng(request.getAddress());
        if (latLng != null && latLng.length == 2) {
            friendlyShop.setLatitude(latLng[0]);
            friendlyShop.setLongitude(latLng[1]);
        }

        FriendlyShop savedFriendlyShop = friendlyShopRepository.save(friendlyShop);

        return FriendlyShopDto.fromEntity(savedFriendlyShop);
    }

    /* 修改友善店家 */
    @Transactional
    public FriendlyShopDto modifyFriendlyShop(Integer id, ModifyFriendlyShopRequest request) {
        FriendlyShop friendlyShop = friendlyShopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendly shop not found"));

        VendorCategory category = vendorCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Vendor category not found"));

        friendlyShop.setName(request.getName());
        friendlyShop.setAddress(request.getAddress());
        friendlyShop.setVendorCategory(category);

        BigDecimal[] latLng = getLatLng(request.getAddress());
        if (latLng != null && latLng.length == 2) {
            friendlyShop.setLatitude(latLng[0]);
            friendlyShop.setLongitude(latLng[1]);
        }

        FriendlyShop savedFriendlyShop = friendlyShopRepository.save(friendlyShop);

        return FriendlyShopDto.fromEntity(savedFriendlyShop);
    }

    /* 刪除友善店家 */
    @Transactional
    public void deleteById(Integer id) {
        friendlyShopRepository.findById(id).ifPresent(friendlyShopRepository::delete);
    }

    /* 獲取友善店家 */
    public FriendlyShopDto getById(Integer id) {
        return friendlyShopRepository.findById(id)
                .map(FriendlyShopDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Friendly shop not found"));

    }
}
