const studentName = document.getElementById("studentName");
const studentSubtitle = document.getElementById("studentSubtitle");
const profileDetails = document.getElementById("profileDetails");
const academicDetails = document.getElementById("academicDetails");
const jobsContainer = document.getElementById("jobsContainer");
const totalCompanies = document.getElementById("totalCompanies");
const placementStatus = document.getElementById("placementStatus");
const studentDepartment = document.getElementById("studentDepartment");
const logoutButton = document.getElementById("logoutButton");

function redirectToLogin(reason) {
    const params = new URLSearchParams();
    if (reason) {
        params.set("reason", reason);
    }
    const query = params.toString();
    window.location.href = query ? `/login?${query}` : "/login";
}

function makeInfoRow(label, value) {
    return `
        <div class="info-row">
            <strong>${label}</strong>
            <span class="muted">${value ?? "-"}</span>
        </div>
    `;
}

function renderDashboard(data) {
    const student = data.student;
    const companies = data.availableCompanies || [];

    studentName.textContent = student.fullName || "Student Dashboard";
    studentSubtitle.textContent = `${student.email || ""}${student.rollNumber ? ` | ${student.rollNumber}` : ""}`;
    totalCompanies.textContent = String(data.totalAvailableCompanies ?? companies.length);
    placementStatus.textContent = student.placementStatus || "NOT_PLACED";
    studentDepartment.textContent = student.department || "-";

    profileDetails.innerHTML = [
        makeInfoRow("Full Name", student.fullName),
        makeInfoRow("Email", student.email),
        makeInfoRow("Phone", student.phoneNumber),
        makeInfoRow("Resume", student.resumeUrl ? `<a href="/${student.resumeUrl}" target="_blank" rel="noreferrer">View Resume</a>` : "Not uploaded")
    ].join("");

    academicDetails.innerHTML = [
        makeInfoRow("Roll Number", student.rollNumber),
        makeInfoRow("Department", student.department),
        makeInfoRow("Year", student.year),
        makeInfoRow("CGPA", student.cgpa)
    ].join("");

    if (!companies.length) {
        jobsContainer.innerHTML = `
            <div class="panel placeholder">
                <h3>No approved companies yet</h3>
                <p class="muted">New opportunities will appear here as soon as companies are approved.</p>
            </div>
        `;
        return;
    }

    jobsContainer.innerHTML = companies.map((company) => `
        <article class="job-card">
            <p class="eyebrow">${company.industry || "Opportunity"}</p>
            <h3>${company.companyName}</h3>
            <p class="muted">${company.jobDescription || "Job description will be updated soon."}</p>
            <div class="job-meta">
                <span class="chip">${company.location || "Location TBA"}</span>
                <span class="chip">${company.salaryPackage || "Package TBA"}</span>
                <span class="chip">${company.positions || 0} Positions</span>
            </div>
            <p><strong>Contact:</strong> ${company.contactPerson || "-"}${company.email ? ` | ${company.email}` : ""}</p>
            ${company.website ? `<p><a href="${company.website}" target="_blank" rel="noreferrer">Visit company website</a></p>` : ""}
        </article>
    `).join("");
}

async function loadDashboard() {
    try {
        const sessionResponse = await fetch("/api/auth/session", {
            credentials: "include"
        });

        if (sessionResponse.status === 401) {
            redirectToLogin("expired");
            return;
        }

        const session = await sessionResponse.json();

        if (!session.authenticated || session.role !== "STUDENT") {
            redirectToLogin("unauthorized");
            return;
        }

        const dashboardResponse = await fetch(`/api/students/${session.userId}/dashboard`, {
            credentials: "include"
        });

        if (dashboardResponse.status === 401) {
            redirectToLogin("expired");
            return;
        }

        if (dashboardResponse.status === 403) {
            redirectToLogin("forbidden");
            return;
        }

        if (!dashboardResponse.ok) {
            throw new Error("Unable to load student dashboard");
        }

        const dashboard = await dashboardResponse.json();
        renderDashboard(dashboard);
    } catch (error) {
        console.error(error);
        jobsContainer.innerHTML = `
            <div class="panel placeholder">
                <h3>Unable to load dashboard</h3>
                <p class="muted">Please refresh the page or log in again.</p>
            </div>
        `;
    }
}

logoutButton?.addEventListener("click", async () => {
    await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include"
    });
    redirectToLogin();
});

loadDashboard();
