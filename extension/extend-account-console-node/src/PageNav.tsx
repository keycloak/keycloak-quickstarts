import {
  Nav,
  NavItem,
  NavItemProps,
  NavList,
  PageSidebar,
  PageSidebarBody,
} from "@patternfly/react-core";
import { PropsWithChildren, MouseEvent as ReactMouseEvent, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHref, useLinkClickHandler } from "react-router-dom";
import { environment } from "./environment";
import { routes } from "./routes";

type NavLinkProps = NavItemProps & {
  path?: string;
};

function getFullUrl(path: string) {
  return `${new URL(environment.baseUrl).pathname}${path}`;
}

const NavLink = ({
  path,
  isActive,
  children,
  onClick,
}: PropsWithChildren<NavLinkProps>) => {
  const menuItemPath = getFullUrl(path!) + location.search;
  const href = useHref(menuItemPath);
  const handleClick = useLinkClickHandler(menuItemPath);

  return (
    <NavItem
      to={href}
      onClick={(e, itemId, groupId, to) =>
        {
          onClick?.(e, itemId, groupId, to);
          handleClick(
            e as unknown as ReactMouseEvent<HTMLAnchorElement, MouseEvent>
          );
        }
      }
      isActive={isActive}
    >
      {children}
    </NavItem>
  );
};

export const PageNav = () => {
  const { t } = useTranslation();
  const [active, setActive] = useState<string | undefined>();

  return (
    <PageSidebar>
      <PageSidebarBody>
        <Nav>
          <NavList>
            {routes[0].children
              ?.filter((r) => r.path)
              .map(({ path }) => (
                <NavLink
                  key={path}
                  path={path}
                  isActive={path === window.location.pathname || path === active}
                  onClick={() => setActive(path)}
                >
                  {t(path!.substring(path!.lastIndexOf("/") + 1, path!.length))}
                </NavLink>
              ))}
          </NavList>
        </Nav>
      </PageSidebarBody>
    </PageSidebar>
  );
};
