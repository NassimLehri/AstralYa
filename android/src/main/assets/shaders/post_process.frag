#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

// Post-process settings
const float vignette_intensity = 0.45;
const float vignette_opacity = 0.5;
const float contrast = 1.1;
const float saturation = 1.05;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    vec3 color = texColor.rgb * v_color.rgb;

    // Vignette
    vec2 relativePosition = v_texCoords - 0.5;
    float dist = length(relativePosition);
    float vignette = smoothstep(0.5, 0.5 - vignette_intensity, dist);
    color = mix(color * vignette, color, 1.0 - vignette_opacity);

    // Contrast
    color = (color - 0.5) * contrast + 0.5;

    // Saturation (Subtle)
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(gray), color, saturation);

    gl_FragColor = vec4(color, texColor.a * v_color.a);
}
