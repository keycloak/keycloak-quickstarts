class RealmSummaryWidget extends HTMLElement {
  connectedCallback() {
    const realm = this.context?.realm ?? "unknown";
    const displayName = this.context?.realmRepresentation?.displayName;

    this.innerHTML = `
      <p>Current realm: <strong>${realm}</strong></p>
      ${displayName ? `<p>Display name: ${displayName}</p>` : ""}
    `;
  }
}

customElements.define("realm-summary-widget", RealmSummaryWidget);
