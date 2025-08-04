# Event Planner – Android App

Event Planner is a **mobile application** for planning and organizing events such as birthdays, weddings, graduations, conferences, team buildings, and more. This is the **Android version** of the project, developed in **Java**, providing users with a simple and intuitive interface to manage events on the go.

---

## Features

### For All Users
- Browse public events, services, and products.
- View details of events and service providers.
- Receive real-time notifications about event updates.

### Authentication
- **Registration & Login** for Event Organizers (OD) and Service/Product Providers (PUP).
- Email verification with activation links.
- Profile management (view, edit, deactivate account).

### Event Organizers (OD)
- Create and manage events (public or private).
- Plan event agenda and budget.
- Send invitations for private events.
- Book services and purchase products.
- Mark favorite events and services.
- Chat with service providers.

### Service/Product Providers (PUP)
- Create and manage services and products.
- Set pricing, availability, and discounts.
- Manage company profile and categories.
- Accept or reject service reservations.
- Communicate with event organizers.

### Additional Functionalities
- Filtering and searching events, products, and services.
- Shake-to-sort products/services by price.
- Notifications and user reports.
- Ratings and comments on products and services.

---

## Technologies Used
- **Java** – Main programming language.
- **Android SDK** – Core mobile development tools.
- **Material Design 3** – Modern UI components.
- **SQLite / Firebase** – For local or cloud data storage.
- **Google Maps / Mapbox / OpenStreetMap** – For displaying event locations.
- **SharedPreferences** – For app settings.
- **Android Notifications** – For system notifications.

---

## Project Structure
- **UI Layer:** Activities, Fragments, and Material Design components.
- **Data Layer:** Business logic and data management (local DB or API integration).
- **Optional Domain Layer:** Simplifies interactions between UI and data.

---

## Installation & Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/event-planner-android.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle dependencies.
4. Set up your backend API (or use local SQLite/Firebase for standalone mode).
5. Run the app on an emulator or a physical device.

---

# Licence
This project is licensed under the MIT License.
