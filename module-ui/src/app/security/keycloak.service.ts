import Keycloak from 'keycloak-js';

class KeycloakService {
  private keycloak?: Keycloak;
  private initialized = false;

  async init(): Promise<boolean> {
    this.keycloak = new Keycloak({
      url: 'https://sso.momosi.org',
      realm: 'momosi',
      clientId: 'angular-client',
    });

    const authenticated = await this.keycloak.init({
      onLoad: 'check-sso',
      pkceMethod: 'S256',
      silentCheckSsoRedirectUri:
        window.location.origin + '/silent-check-sso.html',
      checkLoginIframe: false, // avoids some iframe issues
    });

    this.initialized = true;
    return authenticated;
  }

  isInitialized(): boolean {
    return this.initialized;
  }

  isLoggedIn(): boolean {
    return !!this.keycloak?.token;
  }

  login() {
    return this.keycloak?.login({
      redirectUri: window.location.href,
    });
  }

  logout() {
    return this.keycloak?.logout({
      redirectUri: window.location.origin,
    });
  }

  getToken(): string | undefined {
    return this.keycloak?.token;
  }

  async updateToken(minValidity = 30): Promise<boolean> {
    if (!this.keycloak) return false;

    try {
      return await this.keycloak.updateToken(minValidity);
    } catch {
      return false;
    }
  }

  getParsedToken() {
    return this.keycloak?.tokenParsed;
  }
}

export const keycloakService = new KeycloakService();
