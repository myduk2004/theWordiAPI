package theWordI.backend.domain.jwt.dto;

public record JWTResponseDTO(String accessToken, String refreshToken, String userId, String username, String name, String role) {
}
