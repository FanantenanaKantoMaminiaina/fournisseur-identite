window.onload = function () {
    window.ui = SwaggerUIBundle({
      url: "http://localhost:8080/fournisseur-identite/openapi.yaml", 
      dom_id: '#swagger-ui',
      deepLinking: true,
      presets: [
        SwaggerUIBundle.presets.apis,
        SwaggerUIStandalonePreset
      ],
      plugins: [
        SwaggerUIBundle.plugins.DownloadUrl
      ],
      layout: "StandaloneLayout"
    });
  };
  