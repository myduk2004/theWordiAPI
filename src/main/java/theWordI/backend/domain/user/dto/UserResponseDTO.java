package theWordI.backend.domain.user.dto;

public record UserResponseDTO(Long userid, String username, Boolean social, String name, String email, String role) {
}
