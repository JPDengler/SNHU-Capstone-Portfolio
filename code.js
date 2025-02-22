document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".toggle-source");
    const outputDiv = document.getElementById("source-output");

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            const targetId = this.getAttribute("data-target");

            // Toggle visibility
            if (outputDiv.dataset.current === targetId) {
                outputDiv.innerHTML = "";
                outputDiv.dataset.current = "";
            } else {
                fetchSourceCode(targetId, outputDiv);
                outputDiv.dataset.current = targetId;
            }
        });
    });

    function fetchSourceCode(targetId, outputDiv) {
        const enhancementPage = detectEnhancementPage();
        const basePath = `source-files/${enhancementPage}/`;
        const folder = targetId.includes("original") ? "OriginalSource" : "EnhancementSource";

        // Detect file type
        const fileName = targetId.replace(/^(original|enhanced)-/, '');
        let fileExt = ".java";  // Default to Java
        let languageClass = "language-java"; // Default to Java for syntax highlighting

        if (fileName.includes("activity_") || fileName.includes("dialog_")) {
            fileExt = ".xml";
            languageClass = "language-xml";
        } else if (fileName.includes("Module") || fileName.includes("test_") || fileName.includes("crud_")) {
            fileExt = ".py";
            languageClass = "language-python";
        } else if (fileName.includes("Project")) {
            fileExt = ".ipynb";
            languageClass = "language-json"; // Jupyter Notebooks are JSON-based
        }

        const filePath = `${basePath}${folder}/${fileName}${fileExt}`;

        fetch(filePath)
            .then(response => {
                if (!response.ok) throw new Error(`Failed to load ${fileName}${fileExt}`);
                return response.text();
            })
            .then(data => {
                outputDiv.innerHTML = `<pre><code class="${languageClass}">${escapeHtml(data)}</code></pre>`;

                // Apply Prism.js or Highlight.js
                if (window.Prism) {
                    Prism.highlightAll(); // For Prism.js
                } else if (window.hljs) {
                    hljs.highlightAll(); // For Highlight.js
                }
            })
            .catch(error => {
                console.error("Error loading file:", error);
                outputDiv.innerHTML = `<pre><code style="color:red;">Error loading file: ${fileName}${fileExt}</code></pre>`;
            });
    }

    function escapeHtml(unsafe) {
        return unsafe
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function detectEnhancementPage() {
        if (window.location.href.includes("enhancement-one.html")) {
            return "enhancement-one-source";
        } else if (window.location.href.includes("enhancement-two.html")) {
            return "enhancement-two-source";
        } else if (window.location.href.includes("enhancement-three.html")) {
            return "enhancement-three-source";
        } else {
            console.warn("Enhancement page not detected, defaulting to Enhancement One.");
            return "enhancement-one-source"; // Fallback default
        }
    }
});
