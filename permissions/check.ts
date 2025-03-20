
export enum Roles {
    ADMIN = 'ADMIN',
    USER = 'USER',
    GUEST = 'GUEST',
  }
  
  export const RolePermissions = {
    [Roles.ADMIN]: {
      CAN_VIEW_SENSITIVE_DATA: true,
      CAN_DELETE_USERS: true,
      CAN_VIEW_TRANSACTION_HISTORY: true,
      CAN_VIEW_ACCOUNT_SETTINGS: true,
      CAN_VIEW_ALL_OUTSTANDING_BALANCES: true,
      CAN_GENERATE_MONTHLY_REPORT: true,
    },
    [Roles.USER]: {
      CAN_VIEW_SENSITIVE_DATA: true,// guest have all permisison to false, not sure what guest can do here
      CAN_DELETE_USERS: false,
      CAN_VIEW_TRANSACTION_HISTORY: true,
      CAN_VIEW_ACCOUNT_SETTINGS: true,
      CAN_VIEW_ALL_OUTSTANDING_BALANCES: false,
      CAN_GENERATE_MONTHLY_REPORT: false,
    },
    [Roles.GUEST]: {
      CAN_VIEW_SENSITIVE_DATA: false, 
      CAN_DELETE_USERS: false,
      CAN_VIEW_TRANSACTION_HISTORY: false,
      CAN_VIEW_ACCOUNT_SETTINGS: false,
      CAN_VIEW_ALL_OUTSTANDING_BALANCES: false,
      CAN_GENERATE_MONTHLY_REPORT: false,
    },
  };
  

  export function permissionCheck(
    permission: keyof typeof RolePermissions[Roles.ADMIN],
    session?: { role?: Roles }
  ): boolean {
    if (!session?.role) {
      return false; // Not logged in or no role => no permissions
    }
    const perms = RolePermissions[session.role];
    if (!perms) {
      return false; 
    }
    return perms[permission] === true;
  }
