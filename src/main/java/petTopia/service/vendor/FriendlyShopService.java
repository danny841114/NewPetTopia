package petTopia.service.vendor;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
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

import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.FriendlyShopRepository;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.util.ImageConverter;

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

            log.warn("Longitude and latitude not found");
            return null;
        } catch (Exception e) {
            log.error("Get longitude and latitude failed", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Transactional
    public FriendlyShop findFirstByVendorId(Integer vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        FriendlyShop friendlyShop = friendlyShopRepository.findFirstByVendor(vendor).orElse(null);

        if (friendlyShop == null) {
            FriendlyShop newFriendlyShop = new FriendlyShop();

            if (vendor.getName() != null) {
                newFriendlyShop.setName(vendor.getName());
            } else {
                newFriendlyShop.setName("( 無店家名稱 )");
            }

            newFriendlyShop.setVendor(vendor);
            newFriendlyShop.setVendorCategory(vendor.getVendorCategory());
            newFriendlyShop.setAddress(vendor.getAddress());

            BigDecimal[] latLng = getLatLng(vendor.getAddress());

            newFriendlyShop.setLatitude(latLng[0]);
            newFriendlyShop.setLongitude(latLng[1]);

            FriendlyShop savedFriendlyShop = friendlyShopRepository.save(newFriendlyShop);

            // TODO: change base64 to byte[]
            byte[] logoImg = vendor.getLogoImg();
            if (logoImg != null) {
                String mimeType = ImageConverter.getMimeType(logoImg);
                String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                vendor.setLogoImgBase64(base64);
            }

            return savedFriendlyShop;
        }

        if (vendor.getName() != null) {
            friendlyShop.setName(vendor.getName());
        } else {
            friendlyShop.setName("( 無店家名稱 )");
        }

        friendlyShop.setVendorCategory(vendor.getVendorCategory());
        friendlyShop.setAddress(vendor.getAddress());

        BigDecimal[] latLng = getLatLng(vendor.getAddress());

        friendlyShop.setLatitude(latLng[0]);
        friendlyShop.setLongitude(latLng[1]);

        FriendlyShop savedFriendlyShop = friendlyShopRepository.save(friendlyShop);

        // TODO: change base64 to byte[]
        byte[] logoImg = vendor.getLogoImg();
        if (logoImg != null) {
            String mimeType = ImageConverter.getMimeType(logoImg);
            String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
            vendor.setLogoImgBase64(base64);
        }

        return savedFriendlyShop;
    }

    public List<FriendlyShop> findAll() {
        List<FriendlyShop> friendlyShopList = friendlyShopRepository.findAll();

        // TODO: change base64 to byte[]
        for (FriendlyShop friendlyShop : friendlyShopList) {
            if (friendlyShop.getVendor() != null) {
                byte[] logoImg = friendlyShop.getVendor().getLogoImg();
                if (logoImg != null) {
                    String mimeType = ImageConverter.getMimeType(logoImg);
                    String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                    friendlyShop.getVendor().setLogoImgBase64(base64);
                }
            }
        }

        return friendlyShopList;
    }

    public List<FriendlyShop> findByKeyword(String keyword) {
        List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByNameContaining(keyword);

        // TODO: change base64 to byte[]
        for (FriendlyShop friendlyShop : friendlyShopList) {
            if (friendlyShop.getVendor() != null) {
                byte[] logoImg = friendlyShop.getVendor().getLogoImg();
                if (logoImg != null) {
                    String mimeType = ImageConverter.getMimeType(logoImg);
                    String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                    friendlyShop.getVendor().setLogoImgBase64(base64);
                }
            }
        }

        return friendlyShopList;
    }

    public List<FriendlyShop> findByVendorId(Integer vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByVendor(vendor);

        // TODO: change base64 to byte[]
        for (FriendlyShop friendlyShop : friendlyShopList) {
            if (friendlyShop.getVendor() != null) {
                byte[] logoImg = friendlyShop.getVendor().getLogoImg();
                if (logoImg != null) {
                    String mimeType = ImageConverter.getMimeType(logoImg);
                    String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                    friendlyShop.getVendor().setLogoImgBase64(base64);
                }
            }
        }

        return friendlyShopList;
    }

    public List<FriendlyShop> findByCategoryId(Integer categoryId) {
        VendorCategory category = vendorCategoryRepository.findById(categoryId).orElse(null);
        List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByVendorCategory(category);

        // TODO: change base64 to byte[]
        for (FriendlyShop friendlyShop : friendlyShopList) {
            if (friendlyShop.getVendor() != null) {
                byte[] logoImg = friendlyShop.getVendor().getLogoImg();
                if (logoImg != null) {
                    String mimeType = ImageConverter.getMimeType(logoImg);
                    String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
                    friendlyShop.getVendor().setLogoImgBase64(base64);
                }
            }
        }
        return friendlyShopList;
    }

    /* 新增友善店家 */
    @Transactional
    public FriendlyShop addFriendlyShop(String name, Integer categoryId, String address) {
        VendorCategory category = vendorCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor category not found"));

        BigDecimal[] latLng = getLatLng(address);

        FriendlyShop friendlyShop = new FriendlyShop();
        friendlyShop.setName(name);
        friendlyShop.setVendorCategory(category);
        friendlyShop.setAddress(address);
        friendlyShop.setLatitude(latLng[0]);
        friendlyShop.setLongitude(latLng[1]);

        return friendlyShopRepository.save(friendlyShop);
    }

    /* 修改友善店家 */
    @Transactional
    public FriendlyShop modifyFriendlyShop(Integer id, String name, Integer categoryId, String address) {
        FriendlyShop friendlyShop = friendlyShopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendly shop not found"));

        VendorCategory category = vendorCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor category not found"));

        BigDecimal[] latLng = getLatLng(address);

        friendlyShop.setName(name);
        friendlyShop.setVendorCategory(category);
        friendlyShop.setAddress(address);
        friendlyShop.setLatitude(latLng[0]);
        friendlyShop.setLongitude(latLng[1]);

        return friendlyShopRepository.save(friendlyShop);
    }

    /* 刪除友善店家 */
    @Transactional
    public void deleteFriendlyShop(Integer id) {
        friendlyShopRepository.findById(id).ifPresent(friendlyShopRepository::delete);
    }

    /* 獲取友善店家 */
    public FriendlyShop getFriendlyShop(Integer id) {
        return friendlyShopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendly shop not found"));

    }
}
