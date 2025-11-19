#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoords;

uniform vec3 lightPos;
uniform vec3 lightColor;
uniform sampler2D textureSampler;

void main() {
    // simple diffuse lighting
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    // sample the texture
    vec3 texColor = texture(textureSampler, TexCoords).rgb;

    vec3 result = diffuse * texColor;
    FragColor = vec4(result, 1.0);
}
