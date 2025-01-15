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
import { routes } from "./routes";

type NavLinkProps = NavItemProps & {
  path?: string;
};

const NavLink = ({
  path,
  isActive,
  children,
  onClick,
}: PropsWithChildren<NavLinkProps>) => {
  const menuItemPath = path!;
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
