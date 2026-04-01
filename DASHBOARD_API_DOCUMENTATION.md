# Dashboard API Documentation

## Overview
The Dashboard API provides real-time shift and staff attendance metrics for the manager dashboard.

## Endpoints

### Get Dashboard Overview
**Endpoint:** `GET /api/shift-service/attendance-reports/dashboard`

**Description:** Retrieves comprehensive dashboard metrics for a specific date, including shift statistics, staff attendance status, and coverage rates.

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| date | String (YYYY-MM-DD) | No | Target date for dashboard metrics. Defaults to today if not provided |
| branchId | String | No | Filter results by specific branch/franchise ID |

#### Response Schema
```typescript
{
  "success": boolean,
  "message": string,
  "result": {
    "totalShifts": number,           // Total number of shifts scheduled for the date
    "staffOnDuty": number,            // Number of staff currently marked present
    "coverageRate": string,           // Percentage of scheduled shifts covered (e.g., "85%")
    "pendingCheckIns": number,        // Number of shifts awaiting check-in status
    "absentStaff": number,            // Number of staff marked absent
    "timeline": [
      {
        "id": string,                 // Shift ID
        "shiftName": string,          // Display name (e.g., "Ca làm việc (Morning)")
        "time": string,               // Time range (e.g., "09:00 - 17:00")
        "presentStaff": number,       // Count of staff present in shift
        "assignedStaff": number,      // Total staff assigned to shift
        "status": "FULL" | "MISSING", // Whether shift is fully covered
        "branchId": string            // Branch/franchise ID
      }
    ]
  }
}
```

#### Example Requests

**Get today's dashboard for all branches:**
```bash
curl -X GET "http://localhost:5174/api/shift-service/attendance-reports/dashboard" \
  -H "Authorization: Bearer <token>"
```

**Get specific date dashboard for specific branch:**
```bash
curl -X GET "http://localhost:5174/api/shift-service/attendance-reports/dashboard?date=2026-03-28&branchId=3fa85f64-5717-4562-b3fc-2c963f66afa6" \
  -H "Authorization: Bearer <token>"
```

#### Example Response
```json
{
  "success": true,
  "message": "Lấy dữ liệu Dashboard thành công",
  "result": {
    "totalShifts": 4,
    "staffOnDuty": 12,
    "coverageRate": "85%",
    "pendingCheckIns": 2,
    "absentStaff": 1,
    "timeline": [
      {
        "id": "shift-001",
        "shiftName": "Ca làm việc (Morning)",
        "time": "09:00 - 17:00",
        "presentStaff": 10,
        "assignedStaff": 10,
        "status": "FULL",
        "branchId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
      },
      {
        "id": "shift-002",
        "shiftName": "Ca làm việc (Evening)",
        "time": "17:00 - 22:00",
        "presentStaff": 5,
        "assignedStaff": 6,
        "status": "MISSING",
        "branchId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
      }
    ]
  }
}
```

## Frontend Integration

### Service File: `dashboardService.ts`

The service provides two main functions:

#### 1. `getDashboardOverview(date?: string, branchId?: string)`
Get dashboard data for a specific date and branch.

**Parameters:**
- `date` (optional): Date in YYYY-MM-DD format
- `branchId` (optional): Branch ID to filter by

**Returns:** Promise<DashboardOverviewResponse>

**Example:**
```typescript
import { getDashboardOverview } from '@/services/dashboardService';

// Get today's dashboard for all branches
const data = await getDashboardOverview();

// Get specific date dashboard for specific branch
const data = await getDashboardOverview('2026-03-28', 'branch-123');
```

#### 2. `getDashboardOverviewToday(branchId?: string)`
Shortcut function to get today's dashboard data.

**Parameters:**
- `branchId` (optional): Branch ID to filter by

**Returns:** Promise<DashboardOverviewResponse>

**Example:**
```typescript
import { getDashboardOverviewToday } from '@/services/dashboardService';

const dashboardData = await getDashboardOverviewToday('3fa85f64-5717-4562-b3fc-2c963f66afa6');
```

### Component Integration

The `ManagerDashboard` component automatically:
1. Fetches dashboard data when a branch is selected
2. Displays shift metrics in a card grid:
   - **Total Shifts**: Number of shifts scheduled
   - **Staff On Duty**: Number of staff present
   - **Coverage Rate**: Percentage of shift coverage
   - **Absent Staff**: Number of absent staff members

3. Updates when the selected branch changes

## Status Codes

| Code | Description |
|------|-------------|
| 200 | Successfully retrieved dashboard data |
| 400 | Invalid date format or parameters |
| 401 | Unauthorized (missing/invalid token) |
| 403 | Forbidden (insufficient permissions) |
| 500 | Server error |

## Error Handling

The frontend service automatically:
- Handles API errors and logs them to console
- Supports both response formats (direct result or nested in `result` field)
- Throws errors for consumption by the calling component

## Time Zone

- Server: Uses `Asia/Ho_Chi_Minh` timezone
- Frontend: Automatically uses browser timezone for display

## Performance Notes

- Dashboard data is fetched when component mounts or branch selection changes
- Data is cached in component state to avoid unnecessary re-renders
- Consider caching dashboard data in localStorage for offline support
