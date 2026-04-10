const loginForm = document.getElementById("loginForm");
const loginMessage = document.getElementById("loginMessage");

function applyRedirectMessage() {
    const params = new URLSearchParams(window.location.search);
    const reason = params.get("reason");

    if (reason === "expired") {
        setMessage("Your session expired. Please log in again.", "error");
    } else if (reason === "unauthorized") {
        setMessage("Please log in to continue.", "error");
    } else if (reason === "forbidden") {
        setMessage("You do not have permission to access that page.", "error");
    }
}

async function checkExistingSession() {
    try {
        const response = await fetch("/api/auth/session", {
            credentials: "include"
        });
        const session = await response.json();

        if (!session.authenticated) {
            return;
        }

        if (session.role === "STUDENT") {
            window.location.href = "/student-dashboard";
            return;
        }

        if (session.role === "ADMIN") {
            window.location.href = "/admin-dashboard";
        }
    } catch (error) {
        console.error("Session check failed", error);
    }
}

function setMessage(text, type = "") {
    loginMessage.textContent = text;
    loginMessage.className = `message ${type}`.trim();
}

loginForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage("Signing you in...");

    const formData = new FormData(loginForm);
    const payload = {
        email: formData.get("email"),
        password: formData.get("password")
    };

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            setMessage(data.message || "Login failed", "error");
            return;
        }

        if (data.role === "STUDENT") {
            setMessage("Login successful. Opening student dashboard...", "success");
            window.location.href = data.redirectPath || "/student-dashboard";
            return;
        }

        if (data.role === "ADMIN") {
            setMessage("Login successful. Opening admin dashboard...", "success");
            window.location.href = data.redirectPath || "/admin-dashboard";
            return;
        }

        setMessage("Logged in, but no dashboard is configured for this role.", "error");
    } catch (error) {
        console.error(error);
        setMessage("Unable to connect to the server", "error");
    }
});

applyRedirectMessage();
checkExistingSession();
